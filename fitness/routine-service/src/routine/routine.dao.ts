import {
  RoutineCreated,
  RoutineUpdated,
  RoutineDeleted,
  TrainingAdded,
  TrainingRemoved,
} from '../core';
import { RoutineEntity } from './routine.entity';
import { Inject, Injectable } from '@nestjs/common';
import { Repository } from 'typeorm';
import { RoutineTrainingEntity } from './routine-training.entity';
import { InjectRepository } from '@nestjs/typeorm';

@Injectable()
export class RoutineDao {
  constructor(
    @InjectRepository(RoutineEntity)
    private routineRepository: Repository<RoutineEntity>,
    @InjectRepository(RoutineTrainingEntity)
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
    await this.routineRepository.update(
      Buffer.from(event.id.value).toString('hex'),
      {
        name: event.name.value,
        description: event.description.value,
        userId: Buffer.from(event.userId.value).toString('hex'),
      },
    );

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
