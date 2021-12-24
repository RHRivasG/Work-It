import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

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

  constructor(formBuilder: FormBuilder) {
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
    console.log("Login issued!")
  }
}
