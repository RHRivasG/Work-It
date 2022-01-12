import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  Router, Resolve,
  RouterStateSnapshot,
  ActivatedRouteSnapshot
} from '@angular/router';
import { Observable, of, throwError } from 'rxjs';
import { catchError, map, mapTo, switchMap } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { Training, TrainingVideo } from '../models/training';

@Injectable()
export class TrainingResolver implements Resolve<Training> {
  constructor(private client: HttpClient){}
  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Training> {
    const id = route.params.id
    return this.client.get<Training>(environment.fitnessApiUrl + '/trainings/' + id)
    .pipe(
      switchMap(training =>
        this.client.get<TrainingVideo>(environment + '/trainings/' + id + '/video/metadata')
        .pipe(
          map(video => ({ ...training, video }))
        )
      )
    )
  }
}
