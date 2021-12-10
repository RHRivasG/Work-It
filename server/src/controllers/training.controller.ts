import { Request, Router } from "express";
import { CreateTrainingCommand } from "application/commands/fitness/trainings/CreateTrainingCommand"
import { UpdateTrainingCommand } from "application/commands/fitness/trainings/UpdateTrainingCommand"
import { CreateTrainingVideoCommand } from "application/commands/fitness/trainings/CreateTrainingVideoCommand"
import { DeleteTrainingCommand } from "application/commands/fitness/trainings/DeleteTrainingCommand";
import { trainingService } from "../services/training.service";
import { trainingRepository as trainingRepository } from "../services/training.repository.service";
import { UpdateTrainingVideoCommand } from "application/commands/fitness/trainings/UpdateTrainingVideoCommand";
import { DeleteTrainingVideoCommand } from "application/commands/fitness/trainings/DeleteTrainingVideoCommand";

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

interface CreateTrainingRequest extends Request<{}, {}, TrainingRequestParameters> { }
interface UpdateTrainingRequest extends Request<{ training: string }, {}, Omit<UpdateTrainingCommand, "id">> { }
interface SetTrainingVideoRequest extends Request<{ training: string }, {}, CreateTrainingVideoCommand> { }

const TrainingController = Router()

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
    .get(async (req, res) => {
        const result = await trainingRepository.find(req.params.training)

        if (!result) return res
            .status(404)
            .json({ msg: `Training with id ${req.params.training} not found` })

        res.json(result)
    })
    .put(async (req: UpdateTrainingRequest, res) => {
        const training = await trainingService.handle(new UpdateTrainingCommand(
            req.params.training,
            req.body.data
        ))

        console.log(training)

        if (training)
            res.json({
                message: 'Ok',
                training: {
                    ...training,
                    categories: Array.from(training.categories)
                }
            })
        else res.json({ message: 'Training was not found' })
    })
    .delete(async (req, res) => {
        await trainingService.handle(new DeleteTrainingCommand(
            req.params.training
        ))

        res.json({
            message: 'Ok',
            id: req.params.training
        })
    })

TrainingController.route('/trainings/:training/video')
    .get(async (req, res) => {
        const training = await trainingRepository.find(req.params.training)

        if (!training) return res
            .status(404)
            .json({ msg: `Training with id ${req.params.training} not found` })

        const video = await trainingRepository.getVideo(training)

        if (video)
            res
                .json({
                    ...training.trainingVideo,
                    video: video.toString('base64')
                })
        else
            res
                .status(404)
                .json({ msg: `Training video not found for training with id: ${req.params.training}` })
    })
    .patch(async (req: SetTrainingVideoRequest, res) => {
        const result = await trainingService.handle(new UpdateTrainingVideoCommand(
            req.params.training,
            req.body.filename,
            req.body.video,
            req.body.ext
        ))

        if (result)
            res
                .json({
                    message: 'Ok',
                    training: result
                })
        else
            res
                .status(404)
                .json({ msg: "Training does not have a video and you didn't provide one" })

    })
    .delete(async (req, res) => {
        await trainingService.handle(new DeleteTrainingVideoCommand(
            req.params.training
        ))

        res.json({
            msg: 'Ok'
        })
    })

export default TrainingController
