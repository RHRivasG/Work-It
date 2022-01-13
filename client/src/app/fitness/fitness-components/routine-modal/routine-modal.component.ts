import { EventEmitter, Input } from '@angular/core';
import { Component, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, ActivatedRouteSnapshot } from '@angular/router';
import { faPlus, faTimes } from '@fortawesome/free-solid-svg-icons';
import { Routine } from '../../models/routine';
import { Training } from '../../models/training';
import { RoutineService } from '../../services/routine.service';

@Component({
  selector: 'wi-routine-modal',
  templateUrl: './routine-modal.component.html',
  styleUrls: ['./routine-modal.component.scss']
})
export class RoutineModalComponent implements OnInit {
  addRoutineIcon = faPlus
  closeModalIcon = faTimes
  creatingRoutine = false
  loading = false
  routineGroup: FormGroup
  shown = false
  @Input()
  training!: Training
  availableRoutines: Routine[] = []
  createdRoutines: string[] = []

  @Output()
  close = new EventEmitter()

  constructor(builder: FormBuilder, private route: ActivatedRoute, private service: RoutineService) {
    this.availableRoutines = route.snapshot.data.routines
    this.routineGroup = builder.group({
      name: ['', Validators.required]
    })
  }

  ngOnInit(): void {
  }

  showCreateRoutine() {
    this.creatingRoutine = true
  }

  createRoutine() {
    const routine = this.routineGroup.get('name')?.value
    console.log(routine)
    this.loading = true
    this.service.create(routine, this.training).subscribe(
      () => {
        this.creatingRoutine = false
        this.loading = false
        this.createdRoutines.push(routine)
      }
    )
  }

  show() {
    this.shown = true
  }

  closeModal() {
    this.close.emit()
  }

  interactWithRoutine(routine: Routine) {
    if (routine.trainings.includes(this.training.id)) {
      this.service.removeTraining({ ...routine, trainings: [] }, this.training).subscribe(
        () => routine.trainings = routine.trainings.filter(id => id != this.training.id)
      )
    } else {
      this.service.addTraining({ ...routine, trainings: [] }, this.training).subscribe(
        () => routine.trainings.push(this.training.id)
      )
    }
  }
}
