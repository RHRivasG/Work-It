import { Routine } from "../../core/routine.aggregate";
import { RoutineCommand } from "../routine.command";
import { RoutineService } from "../routine.service";

export class DeleteRoutine implements RoutineCommand {
  constructor(private readonly id: Uint8Array) {}
  async execute(service: RoutineService) {
    const routine: Routine = await service.getRoutine(this.id);
    routine.destroy();
    service.publish(routine.getEvents());
  }
}