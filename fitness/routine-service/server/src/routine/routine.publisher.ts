import { Injectable } from '@nestjs/common';
import { EventBus } from '@nestjs/cqrs';
import { RoutinePublisher as Publisher } from '../../../application/routine.publisher';
import { RoutineEvent } from '../../../core/routine.aggregate';

Injectable();
export class RoutinePublisher implements Publisher {
  constructor(private eventBus: EventBus) {}
  publish(events: RoutineEvent[]): void {
    this.eventBus.publish(events);
  }
}
