import { RoutineService } from "./routine.service";

export interface RoutineCommand {
  execute(service: RoutineService): void;
}
