import { Module } from '@nestjs/common';
import { CqrsModule } from '@nestjs/cqrs';
import { TypeOrmModule } from '@nestjs/typeorm';
import { RoutineService } from '../../../application/routine.service';
import { RoutineCreatedHandler } from './handlers/routine-created.handler';
import { RoutineDeletedHandler } from './handlers/routine-deleted.handler';
import { RoutineUpdatedHandler } from './handlers/routine-updated.handler';
import { TrainingAddedHandler } from './handlers/training-added.handler';
import { TrainingRemovedHandler } from './handlers/training-removed.handler';
import { RoutineController } from './routine.controller';
import { RoutineDao } from './routine.dao';
import { RoutinePublisher } from './routine.publisher';
import { RoutineRepository } from './routine.repository';

@Module({
  imports: [
    CqrsModule,
    TypeOrmModule.forFeature([RoutineDao, RoutineRepository]),
  ],
  controllers: [RoutineController],
  providers: [
    RoutinePublisher,
    RoutineCreatedHandler,
    RoutineUpdatedHandler,
    RoutineDeletedHandler,
    TrainingAddedHandler,
    TrainingRemovedHandler,
    {
      provide: RoutineService,
      useFactory: (
        publisher: RoutinePublisher,
        repository: RoutineRepository,
      ) => {
        return new RoutineService(repository, publisher);
      },
      inject: [RoutinePublisher, RoutineRepository],
    },
  ],
})
export class RoutineModule {}