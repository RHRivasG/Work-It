import { RoutineId } from "../values_objects/routine-id.value";
import { RoutineTrainingId } from "../values_objects/routine-training-id.value";

export class TrainingRemoved {
  constructor(public id: RoutineId, public trainingId: RoutineTrainingId) {}
}
