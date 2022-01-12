import { Component, Inject, OnInit, ViewChild } from '@angular/core';
import { faEllipsisV, faGripLines, faPlayCircle, faTimes } from '@fortawesome/free-solid-svg-icons';
import { moveItemInArray } from "@angular/cdk/drag-drop"
import { Overlay, OverlayRef } from '@angular/cdk/overlay';
import { CdkPortal } from '@angular/cdk/portal';
import { ActivatedRoute } from '@angular/router';
import { FullRoutine } from '../../models/routine';
import { Training } from '../../models/training';
import { RoutineService } from '../../services/routine.service';
import { GlobalSearch, WI_GLOBAL_SEARCH } from 'src/app/services/global-search';
import { HttpClient } from '@angular/common/http';
import { environment } from 'src/environments/environment';

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

  constructor(private http: HttpClient, private route: ActivatedRoute, private service: RoutineService, overlay: Overlay, @Inject(WI_GLOBAL_SEARCH) private search: GlobalSearch<Training>) {
    route.data.subscribe(data => {
      this.routine = data.routine
      this.search.dataSource = this.routine.trainings || []
      this.search.extractor = JSON.stringify
      this.search.result.subscribe(trainings => {
        this.trainings = trainings
      })
    })
    this.updateRef = overlay.create({
      positionStrategy: overlay.position().global().centerHorizontally().centerVertically(),
      hasBackdrop: true,
      width: '30%'
    })
  }

  ngOnInit(): void {
    this.trainings.forEach(t => {
      const trainerId = t.trainerId
      t.trainerId = ''
      this.http.get<{ name: string }>(environment.socialApiUrl + "/profile/public/" + trainerId)
      .subscribe(profile => {
        t.trainerId = profile.name
      })
    })
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
