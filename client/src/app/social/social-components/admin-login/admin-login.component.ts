import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'wi-admin-login',
  templateUrl: './admin-login.component.html',
  styleUrls: ['./admin-login.component.scss']
})
export class AdminLoginComponent implements OnInit {
  loginForm: FormGroup
  inLogin = false

  constructor(builder: FormBuilder, private http: HttpClient, private router: Router) {
    this.loginForm = builder.group({
      token: ['']
    })
  }

  ngOnInit(): void {
  }

  login() {
    this.inLogin = true
    this.http.post(environment.authApiUrl + "/login/admin/" + this.loginForm.get('token')?.value, {}, { responseType: 'text' })
    .subscribe(
      () => {
        this.inLogin = false
        this.router.navigate(['/social', 'dashboard'])
      },
      () => {
        // this.router.navigate(['/social', 'auth', 'login'])
      }
    )
  }

}
