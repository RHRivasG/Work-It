import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { ActivatedRoute, Router, UrlSegment } from '@angular/router';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(protected router: Router) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    let req = request
    return next.handle(req)
      .pipe(
        catchError((error: HttpErrorResponse) => {
          const urlSnapshot = this.router.url
          if (error.status == 401 && !urlSnapshot.includes("login")) {
            this.router.navigate(['/social/auth/login'], { queryParams: { as: 'participant' }})
            return throwError(error)
          }
          return throwError(error)
        })
      );
  }
}
