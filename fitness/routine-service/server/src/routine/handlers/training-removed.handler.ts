import { IEventHandler } from '@nestjs/cqrs';
import { TrainingRemoved } from 'core';
import { RoutineDao } from '../routine.dao';

export class TrainingRemovedHandler implements IEventHandler<TrainingRemoved> {
  constructor(private dao: RoutineDao) {}
  async handle(event: TrainingRemoved) {
    await this.dao.removeTraining(event);
  }
}
