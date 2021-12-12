import { EventEmitter } from '@angular/core';
import { Component, OnInit, Output } from '@angular/core';
import { faTimes } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'wi-routine-update-modal',
  templateUrl: './routine-update-modal.component.html',
  styleUrls: ['./routine-update-modal.component.scss']
})
export class RoutineUpdateModalComponent implements OnInit {
  closeIcon = faTimes
  @Output()
  close = new EventEmitter()

  constructor() { }

  ngOnInit(): void {
  }

  closeModal() {
    this.close.emit()
  }

}
