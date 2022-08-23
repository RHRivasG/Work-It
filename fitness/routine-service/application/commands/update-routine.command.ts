import {
  Routine,
  RoutineDescription,
  RoutineName,
  RoutineTrainings,
  RoutineUserId,
} from "core";
import { RoutineCommand } from "../routine.command";
import { RoutineService } from "../routine.service";

export class UpdateRoutine implements RoutineCommand {
  constructor(
    private readonly id: string,
    private readonly name: string,
    private readonly description: string,
    private readonly userId: string,
    private readonly trainings: string[]
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
