import { EventsHandler, IEventHandler } from '@nestjs/cqrs';
import { RoutineCreated } from '../../core';
import { RoutineDao } from '../routine.dao';

@EventsHandler(RoutineCreated)
export class RoutineCreatedHandler implements IEventHandler<RoutineCreated> {
  constructor(private dao: RoutineDao) {}
  async handle(event: RoutineCreated) {
    console.log(event);
    await this.dao.insert(event);
  }
}
