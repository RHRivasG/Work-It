import { Routine } from '../../core';
import { RoutineCommand } from '../routine.command';
import { RoutineService } from '../routine.service';

export class DeleteRoutine implements RoutineCommand {
  constructor(private readonly id: string) {}
  async execute(service: RoutineService) {
    const routine: Routine = await service.getRoutine(this.id);
    routine.destroy();
    service.publish(routine.getEvents());
  }
}
