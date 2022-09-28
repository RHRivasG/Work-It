import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class IdentityStorageService {

  constructor() { }

  store(identity: string) {
    localStorage.setItem('identity', identity)
  }
}
