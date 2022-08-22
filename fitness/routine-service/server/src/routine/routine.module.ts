import { Module } from '@nestjs/common';
import { CqrsModule } from '@nestjs/cqrs';
import { TypeOrmModule } from '@nestjs/typeorm';
import { RoutineService } from 'application';
import { RoutineCreatedHandler } from './handlers/routine-created.handler';
import { RoutineDeletedHandler } from './handlers/routine-deleted.handler';
import { RoutineUpdatedHandler } from './handlers/routine-updated.handler';
import { TrainingAddedHandler } from './handlers/training-added.handler';
import { TrainingRemovedHandler } from './handlers/training-removed.handler';
import { RoutineController } from './routine.controller';
import { RoutinePublisher } from './routine.publisher';
import { RoutineRepository } from './routine.repository';
import { AuthModule } from '../auth/auth.module';
import { RoutineEntity } from './routine.entity';
import { RoutineTrainingEntity } from './routine-training.entity';

@Module({
  imports: [
    AuthModule,
    CqrsModule,
    TypeOrmModule.forFeature([RoutineEntity, RoutineTrainingEntity]),
  ],
  providers: [
    RoutinePublisher,
    RoutineRepository,
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
  controllers: [RoutineController],
})
export class RoutineModule {}
