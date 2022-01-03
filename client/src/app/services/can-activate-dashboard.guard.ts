import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, CanLoad, Route, Router, RouterStateSnapshot, UrlSegment, UrlTree } from '@angular/router';
import { Observable, of } from 'rxjs';
import { catchError, mapTo } from 'rxjs/operators';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CanActivateDashboardGuard implements CanActivate, CanLoad {
  constructor(private http: HttpClient, private router: Router) {}

  private isAdmin() {
    return this.http.post(environment.socialApiUrl + "/login/admin", {})
    .pipe(
      mapTo(true),
      catchError(_ => of(this.router.createUrlTree(['/social', 'auth', 'login'], { queryParams: { as: 'Participant' }})))
    )
  }

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree
  {
    return this.isAdmin()
  }
  canLoad(
    route: Route,
    segments: UrlSegment[]): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    return this.isAdmin();
  }
}
