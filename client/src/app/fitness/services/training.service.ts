import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { of } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { Training, TrainingVideo } from '../models/training';

@Injectable({
  providedIn: 'root'
})
export class TrainingService {

  constructor(private client: HttpClient) { }

  create(name: string, description: string, categories: string[], video: TrainingVideo) {
    const trainingDto = {
      name,
      description,
      categories
    }
    const training = {
      video,
      ...trainingDto
    }
    return this.client.post(
      environment.fitnessApiUrl + "/trainings",
      trainingDto,
      { responseType: "text" }
    ).pipe(
      switchMap(id => {
        return this.client.post(
          environment.fitnessApiUrl + "/trainings/" + id + "/video",
          training.video,
          { responseType: 'text' }
        )
      })
    )
  }

  update(training: Training){
    const { video, trainerId, ...trainingDto } = training
    return this.client.put(
      environment.fitnessApiUrl + "/trainings/" + training.id,
      trainingDto,
      { responseType: "text" }
    ).pipe(
      switchMap(txt => {
        if (!training.video.video) return of(txt)
        return this.client.post(
          environment.fitnessApiUrl + "/trainings/" + training.id + "/video",
          video,
          { responseType: 'text' }
        )
      })
    )
  }

  delete(training: Training){
    return this.client.delete(
      environment.fitnessApiUrl + "/trainings/" + training.id,
      { responseType: "text" }
    )
  }
}
