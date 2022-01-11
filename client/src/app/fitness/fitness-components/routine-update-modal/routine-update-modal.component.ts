import { EventEmitter, Input } from '@angular/core';
import { Component, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { faTimes } from '@fortawesome/free-solid-svg-icons';
import { FullRoutine } from '../../models/routine';
import { RoutineService } from '../../services/routine.service';

@Component({
  selector: 'wi-routine-update-modal',
  templateUrl: './routine-update-modal.component.html',
  styleUrls: ['./routine-update-modal.component.scss']
})
export class RoutineUpdateModalComponent implements OnInit {
  closeIcon = faTimes
  @Input()
  routine!: FullRoutine
  @Output()
  close = new EventEmitter()
  routineUpdateForm: FormGroup
  loading = false

  constructor(formBuilder: FormBuilder, private service: RoutineService) {
    this.routineUpdateForm = formBuilder.group({
      name: ['', Validators.required],
      description: ['', Validators.required]
    })
  }

  ngOnInit(): void {
  }

  update() {
    this.loading = true
    this.routine.name = this.routineUpdateForm.get('name')?.value
    this.routine.description = this.routineUpdateForm.get('description')?.value
    this.service.update(this.routine).subscribe(
      () => this.loading = false
    )
  }

  closeModal() {
    this.close.emit()
  }

}
