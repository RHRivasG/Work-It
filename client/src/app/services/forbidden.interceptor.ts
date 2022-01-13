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
import { Location } from '@angular/common';

@Injectable()
export class ForbiddenInterceptor implements HttpInterceptor {

  constructor(private location: Location) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    return next.handle(request)
      .pipe(
        catchError((error: HttpErrorResponse) => {
          if (error.status == 403) {
             this.location.back()
             return throwError(error)
          }
          return throwError(error)
        })
      );
  }
}
