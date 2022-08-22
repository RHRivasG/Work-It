import { BaseEntity, Entity, PrimaryColumn, Column } from 'typeorm';

@Entity('routine_training')
export class RoutineTrainingEntity extends BaseEntity {
  @PrimaryColumn({ name: 'id_routine' })
  idRoutine: string;
  @PrimaryColumn({ name: 'id_training' })
  idTraining: string;
  @Column()
  order: number;
}
