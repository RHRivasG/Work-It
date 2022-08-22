import { BaseEntity, Entity, PrimaryColumn, Column } from 'typeorm';

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
}
