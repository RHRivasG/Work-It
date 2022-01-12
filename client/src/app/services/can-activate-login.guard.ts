import { Inject, Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, CanLoad, Route, Router, RouterStateSnapshot, UrlSegment, UrlTree } from '@angular/router';
import { Observable, of } from 'rxjs';
import { catchError, map, mapTo } from 'rxjs/operators';
import { IdentityProvider, WI_IDENTITY_PROVIDER } from './identity-provider';

@Injectable({
  providedIn: 'root'
})
export class CanActivateLoginGuard implements CanActivate, CanLoad {
  constructor(@Inject(WI_IDENTITY_PROVIDER) private identityProvider: IdentityProvider, private router: Router) {}

  get isNotLoggedIn() {
    return this.identityProvider.identity.pipe(
      map(id => {
        if (id == "admin") return this.router.createUrlTree(['/social', 'dashboard'])
        else return this.router.createUrlTree(['/social/profile', id])
      }),
      catchError(() => of(true))
    )
  }

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    return this.isNotLoggedIn;
  }
  canLoad(
    route: Route,
    segments: UrlSegment[]): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    return this.isNotLoggedIn;
  }
}
