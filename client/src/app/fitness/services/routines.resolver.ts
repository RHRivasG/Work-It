import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  Router, Resolve,
  RouterStateSnapshot,
  ActivatedRouteSnapshot
} from '@angular/router';
import { Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { Routine } from '../models/routine';

@Injectable({
  providedIn: 'root'
})
export class RoutinesResolver implements Resolve<Routine[]> {
  constructor (private client: HttpClient){}
  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Routine[]> {
    return this.client.get<Routine[]>(environment.fitnessApiUrl + '/routines')
    .pipe(
      map(t => t?.map(r => ({ ...r, trainings: r.trainings || [] })))
    )
  }
}
