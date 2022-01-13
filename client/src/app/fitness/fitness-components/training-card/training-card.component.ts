import { Overlay, OverlayRef } from '@angular/cdk/overlay';
import { CdkPortal } from '@angular/cdk/portal';
import { HttpClient } from '@angular/common/http';
import { AfterViewInit, Component, ElementRef, EventEmitter, Input, OnDestroy, OnInit, Output, ViewChild } from '@angular/core';
import { faAngleDown, faEllipsisV, faFlag, faPlus, faSpinner } from '@fortawesome/free-solid-svg-icons';
import { of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Trainer } from 'src/app/social/models/trainer';
import { environment } from 'src/environments/environment';
import { Training } from '../../models/training';

@Component({
  selector: 'wi-training-card',
  templateUrl: './training-card.component.html',
  styleUrls: ['./training-card.component.scss']
})
export class TrainingCardComponent implements OnInit, OnDestroy, AfterViewInit {
  moreIcon = faEllipsisV
  addIcon = faPlus
  reportIcon = faFlag
  showMoreIcon = faAngleDown
  descriptionShown = false
  overlayRef: OverlayRef
  trainerName!: String
  spinner = faSpinner
  @Input()
  editing = false
  @Input()
  training!: Training
  @Output()
  options = new EventEmitter()
  @ViewChild(CdkPortal)
  modal!: CdkPortal
  @ViewChild("video")
  video!: ElementRef

  constructor(overlay: Overlay, private http: HttpClient) {
    this.overlayRef = overlay.create({
      positionStrategy: overlay.position().global().centerHorizontally().centerVertically(),
      hasBackdrop: true,
      width: '25%',
    })
  }

  ngAfterViewInit(): void {
    const video = <HTMLVideoElement> this.video.nativeElement

    this.http
    .get(environment.fitnessApiUrl + "/trainings/" + this.training.id + "/video", { responseType: 'text' })
    .pipe(
      catchError(_ => of())
    )
    .subscribe(async file => {
      const url = "data:video/mp4;base64," + file,
            r = await fetch(url),
            blob = await r.blob()

      console.log(blob)

      video.src = URL.createObjectURL(blob)
    })
  }

  ngOnDestroy(): void {
    this.overlayRef.dispose()
  }

  ngOnInit(): void {
    console.log(this.training)
    this.http.get<{ name: string }>(environment.socialApiUrl + "/profile/public/" + this.training.trainerId)
    .subscribe(profile => {
      this.trainerName = profile.name
    })
  }

  toggleDescription() {
    this.descriptionShown = !this.descriptionShown
  }

  addToRoutine() {
    this.overlayRef.attach(this.modal)
  }

  optionsClicked() {
    this.options.emit()
  }
}
