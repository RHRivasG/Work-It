import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { faCircleNotch } from '@fortawesome/free-solid-svg-icons';
import { environment } from 'src/environments/environment';
import { Participant } from '../../models/participant';
import { RequestStatus } from '../../models/request-status';
import { Trainer } from '../../models/trainer';

type Profile = Trainer | Participant

function isParticipant(profile: Trainer | Participant): profile is Participant {
  return "requestStatus" in profile
}

@Component({
  selector: 'wi-profile',
  templateUrl: './profile.component.html',
  styles: [`
    .status-message {
      padding-bottom: 0 !important;
    }
    .fade-in {
      animation: fade-in 400ms;
    }
  `]
})
export class ProfileComponent implements OnInit {
  profileForm: FormGroup
  updateResult: boolean | undefined | null = undefined
  deletionInProgress = false
  preferences!: string[]
  loadingIcon = faCircleNotch
  sendingRequest: boolean = false
  profile!: Profile

  constructor(private builder: FormBuilder, private route: ActivatedRoute, private http: HttpClient, private router: Router) {
    this.profileForm = builder.group({
      username: ['', Validators.compose([Validators.required, Validators.maxLength(50)])],
      preferences: ['']
    })
    route.data.subscribe(data => {
      this.profile = data.profile
      this.preferences = data.preferences
      this.profileForm.get('username')?.setValue(this.profile.name)
      this.profileForm.get('preferences')?.setValue(this.profile.preferences)
    })
  }

  ngOnInit(): void {
  }

  get slug() {
    console.log(isParticipant(this.profile))
    if (isParticipant(this.profile)) return "participants"
    else return "trainers"
  }

  get requestStatus() {
    if (isParticipant(this.profile)) return this.profile.requestStatus
    else return undefined
  }

  updateProfile() {
    this.updateResult = null
    this.http.put(environment.socialApiUrl + `/${this.slug}/` + this.profile.id, {
      name: this.profileForm.get('username')?.value || '',
      preferences: this.profileForm.get('preferences')?.value || ''
    }, { responseType: 'text' })
    .subscribe(
      () => this.updateResult = true,
      () => this.updateResult = false
    )
  }

  deleteAccount() {
    this.deletionInProgress = true
    this.http.delete(environment.socialApiUrl + `/${this.slug}/` + this.profile.id, { responseType: 'text' })
    .subscribe(() => {
      this.deletionInProgress = false
      this.router.navigate(['/social/auth/register'])
    }, () => this.deletionInProgress = false)
  }

  sendRequest() {
    const profile = this.profile
    if (isParticipant(profile)) {
      this.sendingRequest = true
      this.http.post(
        environment.socialApiUrl + "/participants/" + this.profile.id + "/request", "",
        { responseType: 'text' }
      )
      .subscribe(
        () => {
          this.sendingRequest = false
          profile.requestStatus = RequestStatus.Pending
        }
      )
    }
  }

}
