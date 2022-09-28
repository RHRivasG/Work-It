import { RoutineDescription } from "../values_objects/routine-description.value";
import { RoutineId } from "../values_objects/routine-id.value";
import { RoutineName } from "../values_objects/routine-name.value";
import { RoutineTrainings } from "../values_objects/routine-trainings.value";
import { RoutineUserId } from "../values_objects/routine-user-id.value";

export class RoutineUpdated {
  constructor(
    public id: RoutineId,
    public name: RoutineName,
    public userId: RoutineUserId,
    public trainings: RoutineTrainings,
    public description: RoutineDescription
  ) {}
}
