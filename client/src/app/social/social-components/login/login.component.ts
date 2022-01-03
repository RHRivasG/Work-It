import { CDK_CONNECTED_OVERLAY_SCROLL_STRATEGY_PROVIDER_FACTORY } from '@angular/cdk/overlay/overlay-directives';
import { HttpClient, HttpErrorResponse, HttpHeaders, HttpResponse } from '@angular/common/http';
import { ThisReceiver } from '@angular/compiler';
import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { faSpinner } from '@fortawesome/free-solid-svg-icons';
import { of } from 'rxjs';
import { catchError, filter, map, mapTo, switchMap } from 'rxjs/operators';
import { IdentityProvider, WI_IDENTITY_PROVIDER } from 'src/app/services/identity-provider';
import { environment } from 'src/environments/environment';

type LoginResponse = {
  id: string | null
  status: number
}

@Component({
  selector: 'wi-login',
  templateUrl: './login.component.html',
  styles: [`
    form {
      animation: fade-in 400ms;
    }
  `]
})
export class LoginComponent implements OnInit {
  loginForm: FormGroup
  loading: boolean = false
  status: number = 0
  loadingIcon = faSpinner
  private as?: string

  get otherRole() {
    if (this.as == "trainer") return "participant"
    else return "trainer"
  }

  get loginAsAnotherMessage() {
    return `Are you a ${this.otherRole}? Log in as a ${this.otherRole}!`
  }

  constructor(
    formBuilder: FormBuilder,
    private http: HttpClient,
    private route: ActivatedRoute,
    private router: Router,
    @Inject(WI_IDENTITY_PROVIDER) private identityProvider: IdentityProvider
  ) {
    this.loginForm = formBuilder.group({
      username: ['', Validators.compose([Validators.required, Validators.maxLength(50)])],
      password: ['', [Validators.required]]
    })
  }

  ngOnInit(): void {
    this.route.queryParams
    .subscribe(({ as }) => {
      if (as != 'participant' && as != 'trainer') {
        this.router.navigate(
          [],
          {
            relativeTo: this.route,
            queryParams: { as: 'participant' },
            replaceUrl: true
          })
      } else {
        this.as = as
      }
    })
  }

  errorsOf(control: string) {
    let errors = this.loginForm.get(control)?.errors
    return errors ? Object.entries(errors) : []
  }

  login() {
    let credentials = `${this.loginForm.get('username')?.value || ''}:${this.loginForm.get('password')?.value || ''}`
    this.loading = true
    this.status = 0
    this.http.post(environment.socialApiUrl + "/login/" + this.as, 'Login', {
      headers: {
        'Authorization': 'Basic ' + btoa(credentials)
      },
      responseType: 'text',
      observe: 'response',
    })
    .pipe(
      map(r => ({ status: r.status })),
      switchMap(({ status }) => {
        return this.identityProvider.identity.pipe(map(id => ({ id, status })))
      }),
      catchError((e: HttpErrorResponse) => {
        return of({ id: null, status: e.status || 500 })
      })
    )
    .subscribe(({ status, id }) => {
      this.loading = false
      this.status = status

      if (id) this.router.navigate(['/social/profile/', id])
    })
  }
}
