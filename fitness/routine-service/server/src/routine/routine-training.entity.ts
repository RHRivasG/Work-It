import { BaseEntity, Entity, PrimaryColumn, Column } from 'typeorm';

@Entity('routine_training')
export class RoutineTrainingEntity extends BaseEntity {
  @PrimaryColumn('id_routine')
  idRoutine: string;
  @PrimaryColumn('id_training')
  idTraining: string;
  @Column()
  order: number;
}
