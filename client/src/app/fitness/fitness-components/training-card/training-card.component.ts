import { Overlay, OverlayRef } from '@angular/cdk/overlay';
import { CdkPortal } from '@angular/cdk/portal';
import { AfterViewInit, Component, Input, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { faAngleDown, faEllipsisV, faFlag, faPlus } from '@fortawesome/free-solid-svg-icons';
import { TrainingDTO } from "core/fitness/trainings/TrainingDTO"

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
  descriptionShown = false;
  overlayRef: OverlayRef
  @Input()
  training!: TrainingDTO
  @ViewChild(CdkPortal)
  modal!: CdkPortal

  constructor(overlay: Overlay) {
    this.overlayRef = overlay.create({
      positionStrategy: overlay.position().global().centerHorizontally().centerVertically(),
      hasBackdrop: true,
      width: '25%',
    })
  }

  ngAfterViewInit(): void {
  }

  ngOnDestroy(): void {
    this.overlayRef.dispose()
  }

  ngOnInit(): void {
  }

  toggleDescription() {
    this.descriptionShown = !this.descriptionShown
  }

  addToRoutine() {
    this.overlayRef.attach(this.modal)
  }
}
