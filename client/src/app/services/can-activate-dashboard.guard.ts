import { HttpClient } from '@angular/common/http';
import { conditionallyCreateMapObjectLiteral } from '@angular/compiler/src/render3/view/util';
import { Inject, Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, CanLoad, Route, Router, RouterStateSnapshot, UrlSegment, UrlTree } from '@angular/router';
import { identity, Observable, of } from 'rxjs';
import { catchError, map, mapTo, switchMap, switchMapTo, tap } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { IdentityProvider, WI_IDENTITY_PROVIDER } from './identity-provider';

@Injectable({
  providedIn: 'root'
})
export class CanActivateDashboardGuard implements CanActivate {
  constructor(private http: HttpClient, private router: Router, @Inject(WI_IDENTITY_PROVIDER) private identityProvider: IdentityProvider) {}

  get isAdmin() {
    return this.identityProvider.identity.pipe(
      map(identity => identity == "admin"),
      catchError(_ =>
        this.http.post(environment.socialApiUrl + "/login/admin", {}).pipe(
          mapTo(true),
          catchError(_ => of(false))
        )
      )
    )
  }

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree
  {
    return this.isAdmin
  }
}
