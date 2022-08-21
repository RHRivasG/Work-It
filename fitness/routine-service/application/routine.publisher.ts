import { RoutineEvent } from "../core/routine.aggregate";

export interface RoutinePublisher {
  publish(events: RoutineEvent[]): void;
}
