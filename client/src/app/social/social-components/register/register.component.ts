import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { faLock, faSpinner, faUserLock } from '@fortawesome/free-solid-svg-icons';
import { of } from 'rxjs';
import { catchError, mapTo, tap } from 'rxjs/operators';
import { environment } from 'src/environments/environment';

const passwordConfirmationMatches = (f: FormGroup) => {
  const password = f.get('password')?.value,
        confirmationControl = f.get('confirmPassword'),
        confirmation = confirmationControl?.value


  if (!password || !confirmation) confirmationControl?.setErrors({ passwordsDoNotMatch: true })
  else if (password != confirmation) confirmationControl?.setErrors({ passwordsDoNotMatch: true })
  else {
    if (confirmationControl.hasError("passwordsDoNotMatch")) {
      confirmationControl?.setErrors(null)
    }
  }
}

@Component({
  selector: 'wi-register',
  templateUrl: './register.component.html',
  styles: [`
    form {
      animation: fade-in 400ms;
    }
    .error {
      @apply text-red-600 text-sm hidden;
    }
  `]
})
export class RegisterComponent implements OnInit {
  confirmPasswordIcon = faLock
  registerForm: FormGroup
  loading: boolean = false
  loadingIcon = faSpinner
  taxonomies: string[] = []
  status = 0

  constructor(formBuilder: FormBuilder, private client: HttpClient, private router: Router) {
    this.registerForm = formBuilder.group({
      username: ['', Validators.required],
      password: ['', Validators.required],
      confirmPassword: [''],
      tags: [''],
    }, { validators: passwordConfirmationMatches })

    this.client.get<string[]>(environment.socialApiUrl + "/participants/preferences")
    .subscribe(preferences => this.taxonomies = preferences)
  }

  ngOnInit(): void {
  }

  register() {
    this.loading = true
    this.client.post(environment.socialApiUrl + "/participants", {
      name: this.registerForm.get('username')?.value,
      password: this.registerForm.get('password')?.value,
      preferences: this.registerForm.get('tags')?.value
    })
    .pipe(
      mapTo({ status: 200 }),
      catchError((e: HttpErrorResponse) => of({ status: e.status || 500 })),
      tap(_ => this.loading = false)
    )
    .subscribe(({ status }) => {
      this.status = status
      if (status >= 200) this.router.navigate(['/social/auth/login'], { queryParams: { as: 'participant' }})
    })
  }
}
