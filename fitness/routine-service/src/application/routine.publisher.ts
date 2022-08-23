import { RoutineEvent } from '../core';

export interface RoutinePublisher {
  publish(events: RoutineEvent[]): void;
}
