import { HttpBackend, HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of, throwError } from 'rxjs';
import { catchError, map, switchMap, tap } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { IdentityProvider } from './identity-provider';
import { IdentityStorageService } from './identity-storage.service';

type Ideable = {
  user: { id: string }
}

type Id = {
  id: string
}

@Injectable({
  providedIn: 'root'
})
export class SynchronizedIdentityProviderService implements IdentityProvider {


  constructor(private client: HttpClient, private storage: IdentityStorageService) {}

  get identity(): Observable<string> {
        return of(localStorage.getItem('identity'))
        .pipe(
          switchMap(id => {
            if (id) return of(id)
            else return this.client.get<Id | Ideable>(
              environment.socialApiUrl + "/profile/",
            ).pipe(
              map(p => {
                return "id" in p ? p.id : p.user.id
              }),
              tap(id => {
                this.storage.store(id)
              })
            )
          })
        )
  }
}
