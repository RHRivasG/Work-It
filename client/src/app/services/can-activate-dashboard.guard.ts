import { HttpClient } from '@angular/common/http';
import { Inject, Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Observable, of } from 'rxjs';
import { catchError, map, mapTo, switchMap, tap } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { IdentityProvider, WI_IDENTITY_PROVIDER } from './identity-provider';

@Injectable({
  providedIn: 'root'
})
export class CanActivateDashboardGuard implements CanActivate {
  constructor(private http: HttpClient, private router: Router, @Inject(WI_IDENTITY_PROVIDER) private identityProvider: IdentityProvider) {}

  private get login() {
    return this.http.post(environment.socialApiUrl + "/login/admin", {}).pipe(
      mapTo(true),
      catchError(_ => of(this.router.createUrlTree(['/social', 'auth', 'login'])))
    )
  }

  get isAdmin() {
    return this.identityProvider.identity.pipe(
      map(identity => identity == "admin"),
      switchMap(proceed => {
        if (proceed) return of(proceed)
        else return this.login
      }),
      catchError(_ => this.login)
    )
  }

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree
  {
    return this.isAdmin
  }
}
