import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'wi-login',
  templateUrl: './login.component.html',
  styles: [`
    .error {
      @apply text-red-600 text-sm hidden;
    }
    input.ng-invalid.ng-dirty,
    input.ng-invalid.ng-touched
    {
      @apply border-2 border-red-600;
      transform: translateY(-10px);
      transition: transform 400ms;
    }
    input.ng-invalid.ng-dirty ~ .error,
    input.ng-invalid.ng-touched ~ .error,
    {
      @apply block;
      animation: fade-up 400ms;
    }

    @keyframes fade-up {
      0% { opacity: 0; transform: translateY(30px); }
      100% { opacity: 1; }
    }
  `]
})
export class LoginComponent implements OnInit {
  loginForm: FormGroup
  loading: boolean = false

  constructor(formBuilder: FormBuilder, private http: HttpClient) {
    this.loginForm = formBuilder.group({
      username: ['', Validators.compose([Validators.required, Validators.maxLength(50)])],
      password: ['', Validators.required]
    })
  }

  ngOnInit(): void {
  }

  errorsOf(control: string) {
    let errors = this.loginForm.get(control)?.errors
    return errors ? Object.entries(errors) : []
  }

  login() {
    let credentials = `${this.loginForm.get('username')?.value || ''}:${this.loginForm.get('password')?.value || ''}`
    console.log(btoa(credentials))

    this.loading = true
    this.http.post(environment.socialApiUrl + "/login/participants", "", {
      headers: {
        'Authorization': `Basic ${btoa(credentials)}`,
        'Content-Type': 'text/plain'
      },
      responseType: 'text'
    })
      .subscribe(result => {
        this.loading = false
      }, (e) => {
        this.loading = false
      })
  }
}
