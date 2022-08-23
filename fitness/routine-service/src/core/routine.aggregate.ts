import { RoutineCreated } from "./events/routine-created.event";
import { RoutineDeleted } from "./events/routine-deleted.event";
import { RoutineUpdated } from "./events/routine-updated.event";
import { TrainingAdded } from "./events/training-added.event";
import { TrainingRemoved } from "./events/training-removed.event";
import { RoutineDescription } from "./values_objects/routine-description.value";
import { RoutineId } from "./values_objects/routine-id.value";
import { RoutineName } from "./values_objects/routine-name.value";
import { RoutineTrainingId } from "./values_objects/routine-training-id.value";
import { RoutineTrainingOrder } from "./values_objects/routine-training-order.value";
import { RoutineTrainings } from "./values_objects/routine-trainings.value";
import { RoutineUserId } from "./values_objects/routine-user-id.value";

export type RoutineEvent =
  | RoutineCreated
  | RoutineUpdated
  | RoutineDeleted
  | TrainingAdded
  | TrainingRemoved;

export class Routine {
  public id: RoutineId;
  private eventRecorder: RoutineEvent[] = [];
  constructor(
    public name: RoutineName,
    public userId: RoutineUserId,
    public description: RoutineDescription,
    public trainings: RoutineTrainings,
    id?: RoutineId
  ) {
    this.id = id || new RoutineId();
  }

  getEvents(): RoutineEvent[] {
    return this.eventRecorder;
  }

  static create(
    name: RoutineName,
    userId: RoutineUserId,
    description: RoutineDescription,
    trainings: RoutineTrainings
  ): Routine {
    const routine = new Routine(name, userId, description, trainings);
    routine.eventRecorder.push(
      new RoutineCreated(
        routine.id,
        routine.name,
        routine.userId,
        routine.trainings,
        routine.description
      )
    );
    return routine;
  }

  addTraining(trainingId: RoutineTrainingId) {
    const existingId = this.trainings.value.includes(trainingId.value);
    if (existingId) {
      return;
    }

    this.trainings.value.push(trainingId.value);
    const lastTraining = new RoutineTrainingOrder(this.trainings.value.length);
    this.eventRecorder.push(
      new TrainingAdded(this.id, trainingId, lastTraining)
    );
  }

  removeTraining(trainingId: RoutineTrainingId) {
    const trainingsFiltered = this.trainings.value.filter(
      (training) => training != trainingId.value
    );
    this.trainings = new RoutineTrainings(
      trainingsFiltered.map((training) => Buffer.from(training).toString("hex"))
    );
    this.eventRecorder.push(new TrainingRemoved(this.id, trainingId));
  }

  update(
    name: RoutineName,
    userId: RoutineUserId,
    description: RoutineDescription,
    trainings: RoutineTrainings
  ) {
    this.name = name;
    this.userId = userId;
    this.description = description;
    this.trainings = trainings;

    this.eventRecorder.push(
      new RoutineUpdated(
        this.id,
        this.name,
        this.userId,
        this.trainings,
        this.description
      )
    );
  }

  destroy() {
    this.eventRecorder.push(new RoutineDeleted(this.id));
  }
}
