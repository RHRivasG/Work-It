import { Training } from "core/fitness/trainings/Training";
import { Request, Router } from "express";
import { CreateTrainingCommand } from "application/commands/fitness/trainings/CreateTrainingCommand"
import { UpdateTrainingCommand } from "application/commands/fitness/trainings/UpdateTrainingCommand"
import { CreateTrainingVideoCommand } from "application/commands/fitness/trainings/CreateTrainingVideoCommand"
import { DeleteTrainingCommand } from "application/commands/fitness/trainings/DeleteTrainingCommand";
import { trainingService } from "../services/training.service";
import { trainingRepository as trainingRepository } from "../services/training.repository.service";
import { UpdateTrainingVideoCommand } from "application/commands/fitness/trainings/UpdateTrainingVideoCommand";

interface TrainingRequest<Params = {}, Body = {}, Query = {}, Locals = {}>
    extends Request<Params, Body, Query, Locals> {
    training?: Training
}

interface TrainingRequestParameters {
    name: string,
    description: string,
    trainerId: string,
    categories: any[],
    video: {
        filename: string,
        src: string
    }
}

interface CreateTrainingRequest extends TrainingRequest<{}, {}, TrainingRequestParameters> { }
interface UpdateTrainingRequest extends TrainingRequest<{ id: string }, {}, UpdateTrainingCommand> { }
interface SetTrainingVideoRequest extends TrainingRequest<{ id: string }, {}, CreateTrainingVideoCommand> { }

const TrainingController = Router()

TrainingController.param('training', async (req: TrainingRequest, _, next, value) => {
    const training = await trainingRepository.find(value)

    if (training) {
        req.training = training
        return next()
    }

    next(new Error(`Training with id ${value} not found`))
})

TrainingController.route('/trainings')
    .get(async (_, res) => {
        const allTrainings = await trainingRepository.getAll()

        res.json(allTrainings)
    }).post(async (req: CreateTrainingRequest, res) => {
        const { body: { name, description, trainerId, categories, video } } = req,
            createCommand = new CreateTrainingCommand(
                name,
                description,
                trainerId,
                categories
            )

        let training = await trainingService.handle(createCommand),
            createVideoCommand = new CreateTrainingVideoCommand(
                training!.id,
                video.filename,
                video.src
            )
        training = (await trainingService.handle(createVideoCommand))!

        res.json({ message: 'Ok', training })

    })

TrainingController.route('/trainings/:training')
    .get((req: TrainingRequest, res) => {
        res.json(req.training!)
    })
    .put(async (req: UpdateTrainingRequest, res) => {
        const training = await trainingService.handle(req.body)

        if (training) res.json({ message: 'Ok', training })
        else res.json({ message: 'Training was not found' })
    })
    .delete(async (req: TrainingRequest, res) => {
        const training = req.training!
        await trainingService.handle(new DeleteTrainingCommand(training.id))

        res.json({
            message: 'Ok',
            id: training.id
        })
    })

TrainingController.route('/trainings/:training/video')
    .get(async (req: TrainingRequest, res, next) => {
        try {
            const training = req.training!,
                video = await trainingRepository.getVideo(training)

            if (video)
                res.json({
                    ...training.trainingVideo,
                    video: video.toString('base64')
                })
            else
                throw new Error('The training has no video')
        } catch (e) {
            next(e)
        }
    })
    .patch(async (req: SetTrainingVideoRequest, res, next) => {
        const training = req.training!,
            result = await trainingService.handle(new UpdateTrainingVideoCommand(
                training.id,
                req.body.filename,
                req.body.video,
                req.body.ext
            ))

        if (result)
            res.json({
                message: 'Ok',
                training: result
            })
        else
            throw new Error("Training does not have a video and you didn't provide one")

    })
    .delete(async (req: TrainingRequest, res) => {
        const training = req.training!

        res.json({
            message: 'Ok'
        })
    })

export default TrainingController
