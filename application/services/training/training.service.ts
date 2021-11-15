import { ApplicationService } from "../application.service";
import { CreateTrainingCommand } from "../../commands/fitness/trainings/CreateTrainingCommand"
import { UpdateTrainingCommand } from "../../commands/fitness/trainings/UpdateTrainingCommand"
import { DeleteTrainingCommand } from "../../commands/fitness/trainings/DeleteTrainingCommand"
import { CreateTrainingVideoCommand } from "../../commands/fitness/trainings/CreateTrainingVideoCommand"
import { UpdateTrainingVideoCommand } from "../../commands/fitness/trainings/UpdateTrainingVideoCommand"
import { Training } from "core/fitness/trainings/Training";
import { TrainingRepository } from "../../repositories/fitness/training.repository.interface";
import { DeleteTrainingVideoCommand } from "../../commands/fitness/trainings/DeleteTrainingVideoCommand";

type Commands =
    CreateTrainingCommand
    | UpdateTrainingCommand
    | DeleteTrainingCommand
    | CreateTrainingVideoCommand
    | UpdateTrainingVideoCommand
    | DeleteTrainingVideoCommand


export class TrainingService implements
    ApplicationService<CreateTrainingCommand, Training>,
    ApplicationService<UpdateTrainingCommand, Training | undefined>,
    ApplicationService<DeleteTrainingCommand, undefined>,
    ApplicationService<CreateTrainingVideoCommand, Training>,
    ApplicationService<UpdateTrainingVideoCommand, boolean | undefined>,
    ApplicationService<DeleteTrainingVideoCommand, true | undefined>
{

    constructor(private repository: TrainingRepository) { }

    handle(command: UpdateTrainingCommand): Promise<Training | undefined>
    handle(command: CreateTrainingCommand): Promise<Training>
    handle(command: DeleteTrainingCommand): Promise<undefined>
    handle(command: CreateTrainingVideoCommand): Promise<Training>
    handle(command: UpdateTrainingVideoCommand): Promise<Training | undefined>
    handle(command: DeleteTrainingVideoCommand): Promise<true | undefined>
    async handle(command: Commands) {
        if (command instanceof CreateTrainingCommand) {
            const training = Training.create({
                name: command.name,
                description: command.description,
                trainerId: command.trainerId,
                categories: command.categories
            })
            await this.repository.save(training)

            return training
        } else if (command instanceof UpdateTrainingCommand) {
            const training = await this.repository.find(command.id)
            if (training) {
                training.update(command.data)
                await this.repository.save(training)

                return training
            }
        } else if (command instanceof DeleteTrainingCommand) {
            await this.handle(new DeleteTrainingVideoCommand(command.id))

            const training = await this.repository.find(command.id)
            if (training) {
                await this.repository.delete(training)
                training.destroy()
            }
        } else if (command instanceof CreateTrainingVideoCommand) {
            const training = await this.repository.find(command.trainingId),
                videoBuffer = Buffer.from(command.video, 'base64')

            training!.setVideo(command.filename, videoBuffer)
            return await this.repository.addVideo(
                training!,
                videoBuffer
            )
        } else if (command instanceof UpdateTrainingVideoCommand) {
            const training = await this.repository.find(command.trainingId).then(t => t!)

            if (!command.video) {
                const videoBuffer = await this.repository.getVideo(training)
                if (!videoBuffer) return
                training.setVideo(command.filename, videoBuffer, command.ext)
                return await this.repository.addVideo(training)
            } else {
                const videoBuffer = Buffer.from(command.video, 'base64')
                training.setVideo(command.filename, videoBuffer, command.ext)
                return await this.repository.addVideo(training, videoBuffer)
            }

        } else if (command instanceof DeleteTrainingVideoCommand) {
            const training = await this.repository.find(command.trainingId)
            if (training) {
                await this.repository.deleteVideo(training)
                training.destroyVideo()
                return true
            }
        }
    }
}
