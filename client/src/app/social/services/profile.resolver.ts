import { HttpClient } from '@angular/common/http';
import { Inject, Injectable } from '@angular/core';
import {
  Router, Resolve,
  RouterStateSnapshot,
  ActivatedRouteSnapshot
} from '@angular/router';
import { Observable, of, throwError } from 'rxjs';
import { catchError, map, mapTo, switchMap } from 'rxjs/operators';
import { IdentityProvider, WI_IDENTITY_PROVIDER } from 'src/app/services/identity-provider';
import { environment } from 'src/environments/environment';
import { Participant } from '../models/participant';
import { RequestStatus } from '../models/request-status';

type UnfinishedParticipant = Omit<Participant, "requesStatus">

@Injectable({
  providedIn: 'root'
})
export class ProfileResolver implements Resolve<Participant> {
  constructor(private client: HttpClient, @Inject(WI_IDENTITY_PROVIDER) private provider: IdentityProvider, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Participant> {
    return  this.provider.identity.pipe(
      switchMap(id => {
        if (!route.params.id && !id) return throwError(new Error("Id not provided"))
        const effectiveId = (route.params.id || id)

        return this.client.get<UnfinishedParticipant>(
          environment.socialApiUrl + "/participants/" + effectiveId
        ).pipe(
          switchMap(unfinishedPart =>
            this.client.get(environment.socialApiUrl + "/participants/" + effectiveId + "/request")
            .pipe(
              mapTo({ ...unfinishedPart, requestStatus: RequestStatus.Pending }),
              catchError(() => of({ ...unfinishedPart, requestStatus: RequestStatus.NotSent }))
            )
          )
        )
      }),
      catchError((e) => {
        this.router.navigate(['/social/auth/login'], { queryParams: { as: 'participant' } })
        return throwError(e)
      })
    )

  }
}
