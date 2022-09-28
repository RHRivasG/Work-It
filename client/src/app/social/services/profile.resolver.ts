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
import { Participant, UnfinishedParticipant } from '../models/participant';
import { RequestStatus } from '../models/request-status';
import { Trainer } from '../models/trainer';

type Profile<A> =  {
  type: string,
  user: A
}

@Injectable({
  providedIn: 'root'
})
export class ProfileResolver implements Resolve<Participant | Trainer> {
  constructor(private client: HttpClient, @Inject(WI_IDENTITY_PROVIDER) private provider: IdentityProvider, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Participant | Trainer> {
    return  this.provider.identity.pipe(
      switchMap(id => {
        if (id != "admin" || route.params.id) return of(id)
        else {
          this.router.navigate(['/social', 'dashboard'])
          return throwError(new Error(id))
        }
      }),
      switchMap(id => {
        if (!route.params.id && !id) return throwError(new Error("Id not provided"))
        const effectiveId = (route.params.id || id)

        return this.client.get<Profile<UnfinishedParticipant | Trainer>>(
          environment.socialApiUrl + "/profile/" + effectiveId
        ).pipe(
          switchMap(profile => {
            if (profile.type == "trainer") return of(profile.user)

            return this.client.get(environment.socialApiUrl + "/participants/" + effectiveId + "/request")
            .pipe(
              mapTo({ ...profile.user, requestStatus: RequestStatus.Pending }),
              catchError(() => of({ ...profile.user, requestStatus: RequestStatus.NotSent }))
            )
          })
        )
      }),
      catchError((e: Error) => {
        console.log(e)
        if (e.message == "admin") return throwError(e)

        this.router.navigate(['/social/auth/login'], { queryParams: { as: 'participant' } })
        return throwError(e)
      })
    )

  }
}
