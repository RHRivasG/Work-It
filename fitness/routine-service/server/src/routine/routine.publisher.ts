import { Injectable } from '@nestjs/common';
import { EventBus } from '@nestjs/cqrs';
import { RoutinePublisher as Publisher } from 'application';
import { RoutineEvent } from 'core';

@Injectable()
export class RoutinePublisher implements Publisher {
  constructor(private eventBus: EventBus) {}
  publish(events: RoutineEvent[]): void {
    this.eventBus.publishAll(events);
  }
}
