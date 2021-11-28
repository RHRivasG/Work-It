import { TrainingRepository } from "application/repositories/fitness/training.repository.interface";
import { Training } from "core/fitness/trainings/Training";
import { Db, GridFSBucket } from "mongodb";
import { Connection } from "mongoose";
import { TrainingModel } from "../../entities/fitness/training.entity";
import { TrainingVideoModel } from "../../entities/fitness/trainingVideo.entity";
import { Readable } from "node:stream"

export class TypeTrainingRepository implements TrainingRepository {
    constructor(private connection: Connection) { }

    async save(training: Training) {
        const exists = await this.find(training.id),
            { trainingVideo, ...toUpdate } = training,
            model = exists ? await TrainingModel
                .findOneAndUpdate({ id: training.id }, toUpdate)
                .populate('trainingVideo') :
                await new TrainingModel(training).populate('trainingVideo')
        await model!.save();
        return model!.asClassObject()
    }

    async find(id: string) {
        const value = await TrainingModel.findOne({ id }).populate('trainingVideo')
        if (value) {
            return value.asClassObject()
        }
    }

    async getAll() {
        const all = await TrainingModel.find({}).populate('trainingVideo')
        return all.map(f => f.asClassObject())
    }

    async delete(training: Training) {
        const result = await TrainingModel.deleteOne({ id: training.id })
        return result.deletedCount > 0
    }

    private async saveTrainingVideo(training: Training) {
        const savedTrainingVideo = await TrainingVideoModel.findOne({ id: training.trainingVideo!.id })
        if (savedTrainingVideo) {
            await savedTrainingVideo.updateOne(training.trainingVideo)
            return training = await this.save(training)
        }
        else {
            const video = await new TrainingVideoModel(training.trainingVideo).save()
            const updatedTraining =
                await TrainingModel
                    .findOneAndUpdate({ id: training.id }, { trainingVideo: video._id })
                    .populate('trainingVideo')

            return updatedTraining!.asClassObject()
        }

    }

    addVideo(training: Training, video: Buffer): Promise<Training>
    addVideo(training: Training): Promise<Training>
    async addVideo(training: Training, video?: Buffer) {
        if (video) {
            const videoStream = Readable.from(video),
                gridfs = new GridFSBucket(this.connection.db as Db, { bucketName: 'trainingvideos' }),
                storedVideo = await gridfs
                    .find({ filename: `${training.trainingVideo!.id}` })
                    .next()

            if (storedVideo) await this.deletePhysicalVideo(training)

            const promise = () => new Promise((res, rej) =>
                videoStream.pipe(gridfs.openUploadStream(`${training.trainingVideo!.id}`))
                    .on('finish', res)
                    .on('error', rej)
            )

            await promise()
        }
        return await this.saveTrainingVideo(training)
    }

    async getVideo(training: Training): Promise<Buffer | undefined> {
        const metadata = training.trainingVideo

        if (metadata?.name) {
            const gridfs = new GridFSBucket(this.connection.db as Db, { bucketName: 'trainingvideos' }),
                chunks: Buffer[] = [],
                stream = gridfs.openDownloadStreamByName(`${training.trainingVideo!.id}`)

            return new Promise<Buffer>((res, rej) => {
                stream
                    .on('data', chunks.push.bind(chunks))
                    .on('error', rej)
                    .on('end', () => res(Buffer.concat(chunks)))
            })
        }
    }

    private async deletePhysicalVideo(training: Training) {
        const gridfs = new GridFSBucket(this.connection.db as Db, { bucketName: 'trainingvideos' }),
            fileMeta = await gridfs.find({ filename: `${training.trainingVideo!.id}` }).next(),
            fileId = fileMeta!._id
        await gridfs.delete(fileId)
        return training
    }

    async deleteVideo(training: Training) {
        await TrainingVideoModel.findOneAndDelete({ id: training.trainingVideo!.id })
        return await this.deletePhysicalVideo(training)
    }
}
