import { Component, OnInit, ViewChild } from '@angular/core';
import { faEllipsisV, faGripLines, faPlayCircle, faTimes } from '@fortawesome/free-solid-svg-icons';
import { moveItemInArray } from "@angular/cdk/drag-drop"
import { Overlay, OverlayRef } from '@angular/cdk/overlay';
import { CdkPortal } from '@angular/cdk/portal';
import { ActivatedRoute } from '@angular/router';
import { FullRoutine } from '../../models/routine';
import { Training } from '../../models/training';
import { RoutineService } from '../../services/routine.service';

@Component({
  selector: 'wi-show-routine',
  templateUrl: './show-routine.component.html',
  styleUrls: ['./show-routine.component.scss']
})
export class ShowRoutineComponent implements OnInit {
  moreIcon = faEllipsisV
  playCircleIcon = faPlayCircle
  dragIcon = faGripLines
  closeIcon = faTimes
  updateRef: OverlayRef
  @ViewChild(CdkPortal)
  updatePortal!: CdkPortal
  trainings: Training[] = []
  routine!: FullRoutine

  constructor(private route: ActivatedRoute, private service: RoutineService, overlay: Overlay) {
    route.data.subscribe(data => {
      this.routine = data.routine
      this.trainings = this.routine.trainings
    })
    this.updateRef = overlay.create({
      positionStrategy: overlay.position().global().centerHorizontally().centerVertically(),
      hasBackdrop: true,
      width: '30%'
    })
  }

  ngOnInit(): void {
  }

  drop(event: any) {
    moveItemInArray(this.trainings, event.previousIndex, event.currentIndex)
    this.routine.trainings = this.trainings
    this.service.update(this.routine).subscribe()
  }

  showUpdateModal() {
    this.updateRef.attach(this.updatePortal)
  }

  removeTraining(training: Training) {
    this.routine.trainings = this.routine.trainings.filter(t => t != training)
    this.trainings = this.routine.trainings
    this.service.removeTraining(this.routine, training).subscribe()
  }
}
