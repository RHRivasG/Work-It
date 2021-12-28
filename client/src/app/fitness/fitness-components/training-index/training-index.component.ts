import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'wi-training-index',
  templateUrl: './training-index.component.html',
  styleUrls: ['./training-index.component.scss']
})
export class TrainingIndexComponent implements OnInit {
  trainingList = [
    {
      name: 'Bulgarian Squat',
      trainerId: 'Pepe Ramirez',
      categories: [{ value: 'Legs' }, { value: 'Back' }, { value: 'Dumbells' }],
      description: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Fusce vel elit eget nunc blandit malesuada at vel ex. Suspendisse volutpat libero et ex elementum ornare. Nullam odio turpis, posuere nec erat eu, congue faucibus sem. Duis convallis pharetra turpis, id vulputate ante vehicula sed. Proin congue posuere lorem, aliquam auctor neque rutrum at. Nulla nec dignissim sapien. In vitae turpis eget erat sodales lacinia sed eu leo. Vivamus efficitur in risus in dictum. Aenean felis mauris, molestie et justo id, auctor iaculis magna. Aliquam non nulla ac leo viverra ultrices at nec erat. Nunc sit amet velit et urna blandit tempus nec vel diam. Suspendisse massa purus, vestibulum ut mattis sed, consectetur et velit. Curabitur egestas bibendum metus, sed posuere felis aliquam sed. Mauris vel dui sapien. Quisque tincidunt semper egestas.',
      trainingVideo: {
        name: 'Famous Person Speech.mp4',
        ext: 'mp4',
        length: 1024
      }
    }
  ]

  constructor() { }

  ngOnInit(): void {
  }

}
