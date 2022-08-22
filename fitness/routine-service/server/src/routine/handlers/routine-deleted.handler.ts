import { IEventHandler } from '@nestjs/cqrs';
import { RoutineDeleted } from '../../../../core/events/routine-deleted.event';
import { RoutineDao } from '../routine.dao';

export class RoutineDeletedHandler implements IEventHandler<RoutineDeleted> {
  constructor(private dao: RoutineDao) {}
  async handle(event: RoutineDeleted) {
    await this.dao.delete(event);
  }
}
