import { EventEmitter } from '@angular/core';
import { Component, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { faPlus, faTimes } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'wi-routine-modal',
  templateUrl: './routine-modal.component.html',
  styleUrls: ['./routine-modal.component.scss']
})
export class RoutineModalComponent implements OnInit {
  addRoutineIcon = faPlus
  closeModalIcon = faTimes
  creatingRoutine = false
  routineGroup: FormGroup
  shown = false
  @Output()
  close = new EventEmitter()

  constructor(builder: FormBuilder) {
    this.routineGroup = builder.group({
      name: ['']
    })
  }

  ngOnInit(): void {
  }

  showCreateRoutine() {
    this.creatingRoutine = true
  }

  createRoutine() {
    this.creatingRoutine = false
  }

  show() {
    this.shown = true
  }

  closeModal() {
    this.close.emit()
  }
}
