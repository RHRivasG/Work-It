import { EventsHandler, IEventHandler } from '@nestjs/cqrs';
import { RoutineDeleted } from '../../core';
import { RoutineDao } from '../routine.dao';

@EventsHandler(RoutineDeleted)
export class RoutineDeletedHandler implements IEventHandler<RoutineDeleted> {
  constructor(private dao: RoutineDao) {}
  async handle(event: RoutineDeleted) {
    await this.dao.delete(event);
  }
}
