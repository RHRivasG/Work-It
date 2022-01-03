import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { switchMap, tap } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { IdentityProvider } from './identity-provider';
import { IdentityStorageService } from './identity-storage.service';

@Injectable({
  providedIn: 'root'
})
export class SynchronizedIdentityProviderService implements IdentityProvider {

  constructor(private client: HttpClient, private storage: IdentityStorageService) { }

  get identity(): Observable<string> {
        return of(localStorage.getItem('identity'))
        .pipe(
          switchMap(id => {
            if (id) return of(id)
            else return this.client.get(
              environment.socialApiUrl + "/identity",
              {
                responseType: 'text'
              }
            )
          }),
          tap(id => this.storage.store(id))
        )
  }
}
