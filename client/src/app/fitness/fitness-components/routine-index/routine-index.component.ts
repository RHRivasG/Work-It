import { Component, OnInit } from '@angular/core';
import { faPlay } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'wi-routine-index',
  templateUrl: './routine-index.component.html',
  styleUrls: ['./routine-index.component.scss']
})
export class RoutineIndexComponent implements OnInit {
  playIcon = faPlay

  constructor() { }

  ngOnInit(): void {
  }

}
