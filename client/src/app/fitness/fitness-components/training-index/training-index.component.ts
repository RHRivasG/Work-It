import { Component, Inject, Input, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { GlobalSearch, WI_GLOBAL_SEARCH } from 'src/app/services/global-search';
import { Training } from '../../models/training';

@Component({
  selector: 'wi-training-index',
  templateUrl: './training-index.component.html',
  styleUrls: ['./training-index.component.scss']
})
export class TrainingIndexComponent implements OnInit {
  trainings!: Training[]

  constructor(private route: ActivatedRoute, @Inject(WI_GLOBAL_SEARCH) private search: GlobalSearch<Training>) {
    route.data.subscribe(data => {
      this.search.dataSource = data.trainings || []
      this.search.extractor = JSON.stringify
      this.search.result.subscribe(trainings => {
        this.trainings = trainings
      })
    })
  }

  ngOnInit(): void {
  }

}
