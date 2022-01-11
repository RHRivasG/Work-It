import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  Router, Resolve,
  RouterStateSnapshot,
  ActivatedRouteSnapshot
} from '@angular/router';
import { forkJoin, Observable, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { FullRoutine, Routine } from '../models/routine';
import { Training } from '../models/training';

@Injectable({
  providedIn: 'root'
})
export class RoutineResolver implements Resolve<FullRoutine> {
  constructor (private client: HttpClient){}
  // bind|flatMap:: M[A], A => M[B] : M[B]
  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<FullRoutine> {
    const id = route.params.id
    return this.client.get<Routine>(environment.fitnessApiUrl + '/routines/'+ id).pipe(
      switchMap((routine: Routine) => {
        if (routine.trainings) {
          return forkJoin(
            routine.trainings.map(id =>
              this.client.get<Training>(environment.fitnessApiUrl + '/trainings/' + id)
            )
          )
          .pipe(map(trainings => ({ ...routine, trainings })))
        } else {
          return of({ ...routine, trainings: [] })
        }
      }),
    )
  }
}
