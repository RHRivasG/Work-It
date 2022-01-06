import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  Router, Resolve,
  RouterStateSnapshot,
  ActivatedRouteSnapshot
} from '@angular/router';
import { Observable, of } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Training } from '../models/training';

@Injectable({
  providedIn: 'root'
})
export class TrainingsResolver implements Resolve<Training[]> {
  constructor (private client: HttpClient){}
  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Training[]> {
    return this.client.get<Training[]>(environment.fitnessApiUrl + '/trainings')
  }
}
