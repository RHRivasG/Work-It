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

  get isAdmin() {
    return this.identityProvider.identity.pipe(
      tap(console.log),
      map(identity => identity == "admin" ? true : this.router.createUrlTree(['/social', 'auth', 'login'])),
      catchError(e => of(this.router.createUrlTree(['/social', 'auth', 'login'])))
    )
  }

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree
  {
    return this.isAdmin
  }
}
