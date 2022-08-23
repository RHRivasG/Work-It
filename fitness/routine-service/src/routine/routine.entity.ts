import {
  BaseEntity,
  Entity,
  PrimaryColumn,
  Column,
  OneToMany,
  JoinColumn,
} from 'typeorm';
import { RoutineTrainingEntity } from './routine-training.entity';

@Entity('routines')
export class RoutineEntity extends BaseEntity {
  @PrimaryColumn()
  id: string;
  @Column()
  name: string;
  @Column({ name: 'user_id' })
  userId: string;
  @Column()
  description: string;

  @OneToMany(() => RoutineTrainingEntity, (rte) => rte.routine)
  @JoinColumn({ name: 'id', referencedColumnName: 'id_routine' })
  trainings: RoutineTrainingEntity[];
}
