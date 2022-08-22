import { Inject, Injectable } from '@nestjs/common';
import { Repository } from 'typeorm';
import { RoutineEntity } from './routine.entity';
import {
  RoutineRepository as IRoutineRepository,
  RoutineDto,
} from 'application';
import {
  Routine,
  RoutineId,
  RoutineName,
  RoutineDescription,
  RoutineUserId,
  RoutineTrainings,
} from 'core';
import { RoutineTrainingEntity } from './routine-training.entity';
import { InjectRepository } from '@nestjs/typeorm';

@Injectable()
export class RoutineRepository implements IRoutineRepository {
  constructor(
    @InjectRepository(RoutineEntity)
    private routineRepository: Repository<RoutineEntity>,
    @InjectRepository(RoutineTrainingEntity)
    private routineTrainingRepository: Repository<RoutineTrainingEntity>,
  ) {}

  async get(uuid: Uint8Array): Promise<RoutineDto> {
    const routine = await this.routineRepository.findOneBy({
        id: Buffer.from(uuid).toString('hex'),
      }),
      trainings = await this.routineTrainingRepository.find({
        where: { idRoutine: Buffer.from(uuid).toString('hex') },
        order: { order: 'ASC' },
      }),
      dto: RoutineDto = {
        id: new Uint8Array(Buffer.from(routine.id, 'hex')),
        name: routine.name,
        description: routine.description,
        userId: new Uint8Array(Buffer.from(routine.userId, 'hex')),
        trainings: trainings.map(
          (training) => new Uint8Array(Buffer.from(training.idTraining, 'hex')),
        ),
      };

    return dto;
  }

  async getAll(userId: Uint8Array): Promise<RoutineDto[]> {
    const routines = await this.routineRepository.find({
        where: { userId: Buffer.from(userId).toString('hex') },
      }),
      dtos: RoutineDto[] = [];
    routines.forEach(async (routine) => {
      const trainings = await this.routineTrainingRepository.find({
          where: { idRoutine: Buffer.from(routine.id).toString('hex') },
          order: { order: 'ASC' },
        }),
        dto: RoutineDto = {
          id: new Uint8Array(Buffer.from(routine.id, 'hex')),
          name: routine.name,
          description: routine.description,
          userId: new Uint8Array(Buffer.from(routine.userId, 'hex')),
          trainings: trainings.map(
            (training) =>
              new Uint8Array(Buffer.from(training.idTraining, 'hex')),
          ),
        };
      dtos.push(dto);
    });
    return dtos;
  }

  async getRoutine(uuid: Uint8Array): Promise<Routine> {
    const routineEntity = await this.routineRepository.findOneBy({
        id: Buffer.from(uuid).toString('hex'),
      }),
      trainings = await this.routineTrainingRepository.find({
        where: { idRoutine: Buffer.from(uuid).toString('hex') },
        order: { order: 'ASC' },
      }),
      routine = new Routine(
        new RoutineName(routineEntity.name),
        new RoutineUserId(
          new Uint8Array(Buffer.from(routineEntity.userId, 'hex')),
        ),
        new RoutineDescription(routineEntity.description),
        new RoutineTrainings(
          trainings.map(
            (training) =>
              new Uint8Array(Buffer.from(training.idTraining, 'hex')),
          ),
        ),
        new RoutineId(new Uint8Array(Buffer.from(routineEntity.id, 'hex'))),
      );

    return routine;
  }
}
