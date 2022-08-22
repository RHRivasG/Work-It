import { RoutineCreated } from '../../../core/events/routine-created.event';
import { RoutineUpdated } from '../../../core/events/routine-updated.event';
import { RoutineDeleted } from '../../../core/events/routine-deleted.event';
import { TrainingAdded } from '../../../core/events/training-added.event';
import { TrainingRemoved } from '../../../core/events/training-removed.event';
import { RoutineEntity as Routine, RoutineEntity } from './routine.entity';
import { Inject, Injectable } from '@nestjs/common';
import { Repository } from 'typeorm';
import { RoutineTrainingEntity } from './routine-training.entity';

@Injectable()
export class RoutineDao {
  constructor(
    @Inject('ROUTINE_REPOSITORY')
    private routineRepository: Repository<RoutineEntity>,
    @Inject('ROUTINE_TRAINING_REPOSITORY')
    private routineTrainingRepository: Repository<RoutineTrainingEntity>,
  ) {}
  async insert(event: RoutineCreated) {
    await this.routineRepository.insert({
      id: Buffer.from(event.id.value).toString('hex'),
      name: event.name.value,
      description: event.description.value,
      userId: Buffer.from(event.userId.value).toString('hex'),
    });

    event.trainings.value.forEach(async (training, index) => {
      await this.routineTrainingRepository.insert({
        idRoutine: Buffer.from(event.id.value).toString('hex'),
        idTraining: Buffer.from(training).toString('hex'),
        order: index,
      });
    });
  }
  async update(event: RoutineUpdated) {
    await this.routineRepository.save({
      id: Buffer.from(event.id.value).toString('hex'),
      name: event.name.value,
      description: event.description.value,
      userId: Buffer.from(event.userId.value).toString('hex'),
    });

    event.trainings.value.forEach(async (training, index) => {
      await this.routineTrainingRepository.save({
        idRoutine: Buffer.from(event.id.value).toString('hex'),
        idTraining: Buffer.from(training).toString('hex'),
        order: index,
      });
    });
  }
  async delete(event: RoutineDeleted) {
    await this.routineRepository.delete({
      id: Buffer.from(event.id.value).toString('hex'),
    });
  }
  async addTraining(event: TrainingAdded) {
    await this.routineTrainingRepository.save({
      idRoutine: Buffer.from(event.id.value).toString('hex'),
      idTraining: Buffer.from(event.trainingId.value).toString('hex'),
      order: event.trainingOrder.value,
    });
  }
  async removeTraining(event: TrainingRemoved) {
    await this.routineTrainingRepository.delete({
      idRoutine: Buffer.from(event.id.value).toString('hex'),
      idTraining: Buffer.from(event.trainingId.value).toString('hex'),
    });
  }
}
