import { Routine } from "../../core/routine.aggregate";
import { RoutineDescription } from "../../core/values_objects/routine-description.value";
import { RoutineName } from "../../core/values_objects/routine-name.value";
import { RoutineTrainings } from "../../core/values_objects/routine-trainings.value";
import { RoutineUserId } from "../../core/values_objects/routine-user-id.value";
import { RoutineCommand } from "../routine.command";
import { RoutineService } from "../routine.service";

export class CreateRoutine implements RoutineCommand {
  constructor(
    private readonly name: string,
    private readonly description: string,
    private readonly userId: Uint8Array,
    private readonly trainings: Uint8Array[]
  ) {}
  execute(service: RoutineService): void {
    const routine = Routine.create(
      new RoutineName(this.name),
      new RoutineUserId(this.userId),
      new RoutineDescription(this.description),
      new RoutineTrainings(this.trainings)
    );
    service.publish(routine.getEvents());
  }
}
