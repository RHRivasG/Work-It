import { Routine } from "../core/routine.aggregate";
import { RoutineDto } from "./routine.dto";

export interface RoutineRepository {
  get(uuid: Uint8Array): Promise<RoutineDto>;
  getAll(userId: Uint8Array): Promise<RoutineDto[]>;
  getRoutine(uuid: Uint8Array): Promise<Routine>;
}
