import { Routine } from "../../core/routine.aggregate";
import { RoutineDescription } from "../../core/values_objects/routine-description.value";
import { RoutineName } from "../../core/values_objects/routine-name.value";
import { RoutineTrainings } from "../../core/values_objects/routine-trainings.value";
import { RoutineUserId } from "../../core/values_objects/routine-user-id.value";
import { RoutineCommand } from "../routine.command";
import { RoutineService } from "../routine.service";

export class UpdateRoutine implements RoutineCommand {
  constructor(
    private readonly id: Uint8Array,
    private readonly name: string,
    private readonly description: string,
    private readonly userId: Uint8Array,
    private readonly trainings: Uint8Array[]
  ) {}
  async execute(service: RoutineService) {
    const routine: Routine = await service.getRoutine(this.id);
    routine.update(
      new RoutineName(this.name),
      new RoutineUserId(this.userId),
      new RoutineDescription(this.description),
      new RoutineTrainings(this.trainings)
    );
    service.publish(routine.getEvents());
  }
}
