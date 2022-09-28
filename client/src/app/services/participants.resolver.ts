import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  Resolve,
  RouterStateSnapshot,
  ActivatedRouteSnapshot
} from '@angular/router';
import { forkJoin, Observable, of } from 'rxjs';
import { catchError, mapTo, switchMap } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { Participant, UnfinishedParticipant } from '../social/models/participant';
import { RequestStatus } from '../social/models/request-status';

@Injectable({
  providedIn: 'root'
})
export class ParticipantsResolver implements Resolve<Participant[]> {
  private constructor(private http: HttpClient) {}

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Participant[]> {
    console.log('Resolving participants')
    return this.http.get<UnfinishedParticipant[]>(environment.socialApiUrl + "/participants")
    .pipe(
      switchMap(ps => ps.length == 0 ? of(ps) : forkJoin(
        ps.map(p =>
          this.http.get(environment.socialApiUrl + "/participants/" + p.id + "/request")
          .pipe(
            mapTo({ ...p, requestStatus: RequestStatus.Pending }),
            catchError(_ => of({ ...p, requestStatus: RequestStatus.NotSent }))
          )
        ))
      )
    )
  }
}
