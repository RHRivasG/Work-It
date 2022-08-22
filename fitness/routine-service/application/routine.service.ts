import { Routine, RoutineEvent } from "core";
import { RoutineDto } from "./routine.dto";
import { RoutinePublisher } from "./routine.publisher";
import { RoutineRepository } from "./routine.repository";

export class RoutineService {
  constructor(
    private repository: RoutineRepository,
    private publisher: RoutinePublisher
  ) {}

  async get(uuid: Uint8Array): Promise<RoutineDto> {
    return await this.repository.get(uuid);
  }

  async getAll(userId: Uint8Array): Promise<RoutineDto[]> {
    return await this.repository.getAll(userId);
  }

  async getRoutine(uuid: Uint8Array): Promise<Routine> {
    return await this.repository.getRoutine(uuid);
  }

  publish(events: RoutineEvent[]) {
    this.publisher.publish(events);
  }
}
