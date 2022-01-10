import { Overlay, OverlayRef } from '@angular/cdk/overlay';
import { TemplatePortal } from '@angular/cdk/portal';
import { Component, Inject, OnInit, TemplateRef, ViewContainerRef } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { faPlus, faTimes } from '@fortawesome/free-solid-svg-icons';
import { GlobalSearch, WI_GLOBAL_SEARCH } from 'src/app/services/global-search';
import { Training } from '../../models/training';
import { TrainingService } from '../../services/training.service';

@Component({
  selector: 'wi-trainer-trainings',
  templateUrl: './trainer-trainings.component.html',
  styles: [
  ]
})
export class TrainerTrainingsComponent implements OnInit {
  addIcon = faPlus
  closeIcon = faTimes
  trainings!: Training[]
  selectedTraining?: Training
  overlayRef: OverlayRef

  constructor(
    route: ActivatedRoute,
    overlay: Overlay,
    private ref: ViewContainerRef,
    private service: TrainingService,
    @Inject(WI_GLOBAL_SEARCH) private search: GlobalSearch<Training>
  ) {
    const data = route.snapshot.data
    this.search.dataSource = data.trainings
    this.search.extractor = JSON.stringify
    this.search.result.subscribe(trainings => {
      this.trainings = trainings
    })
    this.overlayRef = overlay.create({
      positionStrategy: overlay.position().global().centerHorizontally().centerVertically(),
      hasBackdrop: true,
      width: '25%',
    })
  }

  ngOnInit(): void {
  }

  closeChoice() {
    this.overlayRef.detach()
  }

  startModal(training: Training, ref: TemplateRef<unknown>) {
    this.overlayRef.attach(new TemplatePortal(ref, this.ref, { $implicit: training }))
  }

  deleteTraining(training: Training) {
    this.service.delete(training).subscribe(
      () => {
        this.closeChoice()
        this.trainings = this.trainings.filter(t => t.id != training.id)
      }
    )
  }
}
