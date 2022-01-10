import { Component, Inject, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { faPlay } from '@fortawesome/free-solid-svg-icons';
import { GlobalSearch, WI_GLOBAL_SEARCH } from 'src/app/services/global-search';
import { Routine } from '../../models/routine';

@Component({
  selector: 'wi-routine-index',
  templateUrl: './routine-index.component.html',
  styleUrls: ['./routine-index.component.scss']
})
export class RoutineIndexComponent implements OnInit {
  playIcon = faPlay
  routines!: Routine[]

  constructor(private route: ActivatedRoute, @Inject(WI_GLOBAL_SEARCH) private search: GlobalSearch<Routine>) {
    route.data.subscribe(data =>{
      this.search.dataSource = data.routines || []
      this.search.extractor = JSON.stringify
      this.search.result.subscribe(routines => {
        this.routines = routines
      })
    })
  }

  ngOnInit(): void {
  }

}
