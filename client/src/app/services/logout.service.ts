import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class LogoutService {

  constructor(private client: HttpClient, private router: Router) { }

  logout() {
    if (environment.production)
      this.client
      .delete(environment.authApiUrl + "logout")
      .subscribe(() => this.router.navigate(['/social', 'auth', 'login']))
  }
}
