import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  Router,
  Resolve,
  RouterStateSnapshot,
  ActivatedRouteSnapshot,
} from '@angular/router';
import { Observable, of } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Summary } from '../models/summary';

@Injectable({
  providedIn: 'root',
})
export class SummaryResolver implements Resolve<Summary> {
  constructor(private http: HttpClient) {}
  resolve(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<Summary> {
    const id = route.params.id;
    return this.http.get<Summary>(
      environment.summaryApiUrl + '/routines/' + id + '/summary'
    );
  }
}
