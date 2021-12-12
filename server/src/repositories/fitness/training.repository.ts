import { TrainingRepository } from "application/repositories/fitness/training.repository.interface";
import { Training } from "core/fitness/trainings/Training";
import { Db, GridFSBucket } from "mongodb";
import { Connection } from "mongoose";
import { Readable } from "node:stream"
import { TrainingModel, TrainingVideoModel } from "../../services/entities.service";

export class TypeTrainingRepository implements TrainingRepository {
    constructor(private connection: Connection) { }

    async save(training: Training) {
        const { trainingVideo, ...toUpdate } = training
        await TrainingModel.findOneAndUpdate(
            { id: training.id },
            { $set: { ...toUpdate, categories: Array.from(toUpdate.categories) } },
            { upsert: true }
        )

        return training
    }

    async find(id: string) {
        const value = await TrainingModel.findOne({ id }).populate('trainingVideo')
        if (value) {
            return value.asClassObject()
        }
    }

    async getAll() {
        const all = await TrainingModel.find().populate('trainingVideo')
        return all.map(f => f.asClassObject())
    }

    async delete(training: Training) {
        const result = await TrainingModel.deleteOne({ id: training.id })
        return result.deletedCount > 0
    }

    private async saveTrainingVideo(training: Training) {
        const trainingVideo = training.trainingVideo!
        const trainingVideoDO = await TrainingVideoModel.findOneAndUpdate({ id: trainingVideo.id }, { $set: trainingVideo }, { upsert: true, new: true })
        await TrainingModel.findOneAndUpdate({ id: training.id }, { $set: { trainingVideo: trainingVideoDO!._id } })

        return training
    }

    addVideo(training: Training, video: Buffer): Promise<Training>
    addVideo(training: Training): Promise<Training>
    async addVideo(training: Training, video?: Buffer) {
        if (video) {
            const videoStream = Readable.from(video),
                gridfs = new GridFSBucket(this.connection.db, { bucketName: 'trainingvideos' }),
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
            const gridfs = new GridFSBucket(this.connection.db, { bucketName: 'trainingvideos' }),
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
        const gridfs = new GridFSBucket(this.connection.db, { bucketName: 'trainingvideos' }),
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
