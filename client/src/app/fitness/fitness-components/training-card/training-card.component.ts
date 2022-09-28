import { Overlay, OverlayRef } from '@angular/cdk/overlay';
import { CdkPortal, TemplatePortal } from '@angular/cdk/portal';
import { HttpClient } from '@angular/common/http';
import {
  AfterViewInit,
  Component,
  ElementRef,
  EventEmitter,
  Input,
  OnDestroy,
  OnInit,
  Output,
  TemplateRef,
  ViewChild,
  ViewContainerRef,
} from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import {
  faAngleDown,
  faEllipsisV,
  faFlag,
  faPlus,
  faSpinner,
  faTimes,
} from '@fortawesome/free-solid-svg-icons';
import { of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Trainer } from 'src/app/social/models/trainer';
import { environment } from 'src/environments/environment';
import { Training } from '../../models/training';

@Component({
  selector: 'wi-training-card',
  templateUrl: './training-card.component.html',
  styleUrls: ['./training-card.component.scss'],
})
export class TrainingCardComponent implements OnInit, OnDestroy, AfterViewInit {
  moreIcon = faEllipsisV;
  closeIcon = faTimes;
  addIcon = faPlus;
  reportIcon = faFlag;
  showMoreIcon = faAngleDown;
  descriptionShown = false;
  overlayRef: OverlayRef;
  trainerName!: String;
  spinner = faSpinner;
  reporting = false;
  @Input()
  editing = false;
  @Input()
  training!: Training;
  @Output()
  options = new EventEmitter();
  @ViewChild(CdkPortal)
  modal!: CdkPortal;
  @ViewChild('video')
  video!: ElementRef;
  reportForm: FormGroup;

  constructor(
    private ref: ViewContainerRef,
    private http: HttpClient,
    overlay: Overlay,
    builder: FormBuilder
  ) {
    this.overlayRef = overlay.create({
      positionStrategy: overlay
        .position()
        .global()
        .centerHorizontally()
        .centerVertically(),
      hasBackdrop: true,
      width: '25%',
    });
    this.reportForm = builder.group({
      reason: ['', Validators.required],
    });
  }

  ngAfterViewInit(): void {
    const video = <HTMLVideoElement>this.video.nativeElement;

    this.http
      .get(environment.trainingApiUrl + '/' + this.training.id + '/video', {
        responseType: 'text',
      })
      .pipe(catchError((_) => of()))
      .subscribe(async (file) => {
        const url = 'data:video/mp4;base64,' + file,
          r = await fetch(url),
          blob = await r.blob();

        console.log(blob);

        video.src = URL.createObjectURL(blob);
      });
  }

  ngOnDestroy(): void {
    this.overlayRef.dispose();
  }

  ngOnInit(): void {
    console.log(this.training);
    this.http
      .get<{ name: string }>(
        environment.socialApiUrl + '/profile/public/' + this.training.trainerId
      )
      .subscribe((profile) => {
        this.trainerName = profile.name;
      });
  }

  toggleDescription() {
    this.descriptionShown = !this.descriptionShown;
  }

  addToRoutine() {
    this.overlayRef.attach(this.modal);
  }

  optionsClicked() {
    this.options.emit();
  }

  startModal(ref: TemplateRef<unknown>) {
    this.overlayRef.attach(
      new TemplatePortal(ref, this.ref, { $implicit: this.training })
    );
  }

  reportTraining() {
    this.reporting = true;
    this.http
      .post(
        environment.reportsApiUrl + '/reports',
        {
          trainingId: this.training.id,
          reason: this.reportForm.get('reason')?.value,
        },
        { observe: 'response', responseType: 'text' }
      )
      .subscribe(() => {
        this.reporting = false;
        this.overlayRef.detach();
      });
  }
}
