import { Injectable } from '@nestjs/common';
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

  async get(uuid: string): Promise<RoutineDto> {
    const routine = await this.routineRepository.findOne({
        where: { id: uuid },
        relations: {
          trainings: true,
        },
      }),
      dto: RoutineDto = {
        id: routine.id,
        name: routine.name,
        description: routine.description,
        userId: routine.userId,
        trainings:
          routine.trainings?.map((training) => training.idTraining) || [],
      };

    return dto;
  }

  async getAll(userId: string): Promise<RoutineDto[]> {
    const routines = await this.routineRepository.find({
        where: { userId: userId },
        relations: {
          trainings: true,
        },
      }),
      dtos: RoutineDto[] = routines.map((routine) => {
        const dto: RoutineDto = {
          id: routine.id,
          name: routine.name,
          description: routine.description,
          userId: routine.userId,
          trainings: routine.trainings.map((training) => {
            return training.idTraining;
          }),
        };
        return dto;
      });
    return dtos;
  }

  async getRoutine(uuid: string): Promise<Routine> {
    const routineEntity = await this.routineRepository.findOneBy({
        id: uuid,
      }),
      trainings = await this.routineTrainingRepository.find({
        where: { idRoutine: uuid },
        order: { order: 'ASC' },
      }),
      routine = new Routine(
        new RoutineName(routineEntity.name),
        new RoutineUserId(routineEntity.userId),
        new RoutineDescription(routineEntity.description),
        new RoutineTrainings(trainings.map((training) => training.idTraining)),
        new RoutineId(routineEntity.id),
      );

    console.log(routine.id, uuid);

    return routine;
  }
}
