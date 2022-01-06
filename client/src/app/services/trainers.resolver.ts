import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  Router, Resolve,
  RouterStateSnapshot,
  ActivatedRouteSnapshot
} from '@angular/router';
import { Observable, of } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Trainer } from '../social/models/trainer';

@Injectable({
  providedIn: 'root'
})
export class TrainersResolver implements Resolve<Trainer[]> {
  constructor(private http: HttpClient) {}
  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Trainer[]> {
    return this.http.get<Trainer[]>(environment.socialApiUrl + "/trainers")
  }
}
