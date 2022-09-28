import { Routine } from '../core';
import { RoutineDto } from './routine.dto';

export interface RoutineRepository {
  get(uuid: string): Promise<RoutineDto>;
  getAll(userId: string): Promise<RoutineDto[]>;
  getRoutine(uuid: string): Promise<Routine>;
}
