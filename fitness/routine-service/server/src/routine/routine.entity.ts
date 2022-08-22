import { BaseEntity, Entity, PrimaryColumn, Column } from 'typeorm';

@Entity('routines')
export class RoutineEntity extends BaseEntity {
  @PrimaryColumn('id')
  id: string;
  @Column()
  name: string;
  @Column('user_id')
  userId: string;
  @Column()
  description: string;
}
