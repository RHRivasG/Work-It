import { Routine, RoutineTrainingId } from "core";
import { RoutineCommand } from "../routine.command";
import { RoutineService } from "../routine.service";

export class AddTraining implements RoutineCommand {
  constructor(
    private readonly id: Uint8Array,
    private readonly trainingId: Uint8Array
  ) {}
  async execute(service: RoutineService) {
    const routine: Routine = await service.getRoutine(this.id),
      training = new RoutineTrainingId(this.trainingId);
    routine.addTraining(training);
    service.publish(routine.getEvents());
  }
}
