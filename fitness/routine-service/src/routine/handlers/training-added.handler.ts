import { EventsHandler, IEventHandler } from '@nestjs/cqrs';
import { TrainingAdded } from '../../core';
import { RoutineDao } from '../routine.dao';

@EventsHandler(TrainingAdded)
export class TrainingAddedHandler implements IEventHandler<TrainingAdded> {
  constructor(private dao: RoutineDao) {}
  async handle(event: TrainingAdded) {
    await this.dao.addTraining(event);
  }
}
