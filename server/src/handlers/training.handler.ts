import { EventBus } from "core/shared/EventBus";
import { CreateTrainingCommand } from "application/commands/fitness/trainings/CreateTrainingCommand"
import { UpdateTrainingCommand } from "application/commands/fitness/trainings/UpdateTrainingCommand"
import { CreateTrainingVideoCommand } from "application/commands/fitness/trainings/CreateTrainingVideoCommand"
import { DeleteTrainingCommand } from "application/commands/fitness/trainings/DeleteTrainingCommand";
import { trainingService } from "../services/training.service";
import { EventHandlerBuilder } from "../services/event-bus.service";

const TrainingHandler = new EventHandlerBuilder()

TrainingHandler.handle((bus: EventBus) =>
    bus
        .forEvents(CreateTrainingCommand)
        .subscribe(e => trainingService.handle(e))
)

TrainingHandler.handle((bus: EventBus) =>
    bus
        .forEvents(UpdateTrainingCommand)
        .subscribe(e => trainingService.handle(e))
)

TrainingHandler.handle((bus: EventBus) =>
    bus
        .forEvents(DeleteTrainingCommand)
        .subscribe(e => trainingService.handle(e))
)

TrainingHandler.handle((bus: EventBus) =>
    bus
        .forEvents(CreateTrainingVideoCommand)
        .subscribe(e => trainingService.handle(e))
)

export default TrainingHandler
