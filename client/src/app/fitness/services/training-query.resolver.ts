import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  Router,
  Resolve,
  RouterStateSnapshot,
  ActivatedRouteSnapshot,
} from '@angular/router';
import { Observable, of } from 'rxjs';
import { map, mapTo, switchMap } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { Training, TrainingVideo } from '../models/training';

@Injectable({
  providedIn: 'root',
})
export class TrainingQueryResolver implements Resolve<Training | undefined> {
  constructor(private http: HttpClient) {}

  resolve(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<Training | undefined> {
    const id = route.queryParams.trainingId;
    if (!id)
      return this.http
        .get(environment.trainingApiUrl + '/trainer')
        .pipe(mapTo(undefined));
    return this.http.get<Training>(environment.trainingApiUrl + '/' + id).pipe(
      switchMap((training) => {
        return this.http
          .get<TrainingVideo>(
            environment.trainingApiUrl + '/' + id + '/video/metadata'
          )
          .pipe(
            map((video) => {
              console.log(video);
              return { ...training, video };
            })
          );
      })
    );
  }
}
