import { IEventHandler } from '@nestjs/cqrs';
import { RoutineUpdated } from '../../../../core/events/routine-updated.event';
import { RoutineDao } from '../routine.dao';

export class RoutineUpdatedHandler implements IEventHandler<RoutineUpdated> {
  constructor(private dao: RoutineDao) {}
  async handle(event: RoutineUpdated) {
    await this.dao.update(event);
  }
}
