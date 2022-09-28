import {
  BaseEntity,
  Entity,
  PrimaryColumn,
  Column,
  ManyToOne,
  JoinColumn,
} from 'typeorm';
import { RoutineEntity } from './routine.entity';

@Entity('routine_training')
export class RoutineTrainingEntity extends BaseEntity {
  @PrimaryColumn({ name: 'id_routine' })
  idRoutine: string;
  @PrimaryColumn({ name: 'id_training' })
  idTraining: string;
  @Column()
  order: number;

  @ManyToOne(() => RoutineEntity, (r) => r.trainings)
  @JoinColumn({ name: 'id_routine', referencedColumnName: 'id' })
  routine: RoutineEntity;
}
