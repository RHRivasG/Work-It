import { IEventHandler } from '@nestjs/cqrs';
import { RoutineCreated } from 'core';
import { RoutineDao } from '../routine.dao';

export class RoutineCreatedHandler implements IEventHandler<RoutineCreated> {
  constructor(private dao: RoutineDao) {}
  async handle(event: RoutineCreated) {
    await this.dao.insert(event);
  }
}
