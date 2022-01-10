import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { faPlay } from '@fortawesome/free-solid-svg-icons';
import { Routine } from '../../models/routine';

@Component({
  selector: 'wi-routine-index',
  templateUrl: './routine-index.component.html',
  styleUrls: ['./routine-index.component.scss']
})
export class RoutineIndexComponent implements OnInit {
  playIcon = faPlay
  routines!: Routine[]

  constructor(private route: ActivatedRoute) {
    route.data.subscribe(data =>{
      this.routines = data.routines
    })
  }

  ngOnInit(): void {
  }

}
