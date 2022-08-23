import { EventsHandler, IEventHandler } from '@nestjs/cqrs';
import { RoutineUpdated } from '../../core';
import { RoutineDao } from '../routine.dao';

@EventsHandler(RoutineUpdated)
export class RoutineUpdatedHandler implements IEventHandler<RoutineUpdated> {
  constructor(private dao: RoutineDao) {}
  async handle(event: RoutineUpdated) {
    await this.dao.update(event);
  }
}
