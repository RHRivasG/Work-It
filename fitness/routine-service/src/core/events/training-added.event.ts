import { RoutineId } from "../values_objects/routine-id.value";
import { RoutineTrainingId } from "../values_objects/routine-training-id.value";
import { RoutineTrainingOrder } from "../values_objects/routine-training-order.value";

export class TrainingAdded {
  constructor(
    public id: RoutineId,
    public trainingId: RoutineTrainingId,
    public trainingOrder: RoutineTrainingOrder
  ) {}
}
