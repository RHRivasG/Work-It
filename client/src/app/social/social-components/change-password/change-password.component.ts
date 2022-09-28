import { BreakpointObserver } from '@angular/cdk/layout';
import { Location } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'wi-change-password',
  templateUrl: './change-password.component.html',
  styles: [
  ]
})
export class ChangePasswordComponent implements OnInit {
  newPasswordGroup: FormGroup
  changingPassword = false
  placeholder = ''
  apiSlug = "/participants/"
  id!: string

  constructor(builder: FormBuilder, private http: HttpClient, private route: ActivatedRoute, private router: Router) {
    this.newPasswordGroup = builder.group({
      password: ['']
    })
  }

  ngOnInit(): void {
    console.log(!("requestStatus" in this.route.snapshot.data.profile))
    if (!("requestStatus" in this.route.snapshot.data.profile)) {
      this.apiSlug = "/trainers/"
    }
    this.route.params
    .subscribe(params => {
      this.id = params.id
    })
  }

  goBack() {
    this.router.navigate(['/social/profile', this.id])
  }

  changePassword() {
    this.changingPassword = true
    this.http.put(
      environment.socialApiUrl + this.apiSlug + this.id + "/password",
      { password: this.newPasswordGroup.get('password')?.value },
      { 'responseType': 'text' })
    .subscribe(() => {
      this.changingPassword = false
      this.goBack()
    })
  }
}
