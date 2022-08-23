import { Routine, RoutineEvent } from "core";
import { RoutineDto } from "./routine.dto";
import { RoutinePublisher } from "./routine.publisher";
import { RoutineRepository } from "./routine.repository";

export class RoutineService {
  constructor(
    private repository: RoutineRepository,
    private publisher: RoutinePublisher
  ) {}

  async get(uuid: string): Promise<RoutineDto> {
    return await this.repository.get(uuid);
  }

  async getAll(userId: string): Promise<RoutineDto[]> {
    return await this.repository.getAll(userId);
  }

  async getRoutine(uuid: string): Promise<Routine> {
    return await this.repository.getRoutine(uuid);
  }

  publish(events: RoutineEvent[]) {
    this.publisher.publish(events);
  }
}
