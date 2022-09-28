import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { FullRoutine, Routine } from '../models/routine';
import { Training } from '../models/training';

function fullToPartial(routine: FullRoutine) {
  return {
    ...routine,
    trainings: routine.trainings.map((t) => t.id),
  };
}

@Injectable({
  providedIn: 'root',
})
export class RoutineService {
  constructor(private client: HttpClient) {}

  create(routineName: string, training: Training) {
    const routine = {
      name: routineName,
      description: null,
      trainings: [training.id],
    };
    return this.client.post(environment.routineApiUrl, routine, {
      responseType: 'text',
    });
  }

  update(routine: FullRoutine) {
    return this.client.put(
      environment.routineApiUrl + '/' + routine.id,
      fullToPartial(routine),
      { responseType: 'text' }
    );
  }

  delete(routine: FullRoutine) {
    return this.client.delete(environment.routineApiUrl + routine.id, {
      responseType: 'text',
    });
  }

  addTraining(routine: FullRoutine, training: Training) {
    return this.client.post(
      environment.routineApiUrl + '/' + routine.id + '/training/' + training.id,
      {},
      { responseType: 'text' }
    );
  }

  removeTraining(routine: FullRoutine, training: Training) {
    return this.client.delete(
      environment.routineApiUrl + '/' + routine.id + '/training/' + training.id,
      { responseType: 'text' }
    );
  }
}
