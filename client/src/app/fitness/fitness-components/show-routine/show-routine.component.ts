import { Component, OnInit, ViewChild } from '@angular/core';
import { faEllipsisV, faGripLines, faPlayCircle, faTimes } from '@fortawesome/free-solid-svg-icons';
import { moveItemInArray } from "@angular/cdk/drag-drop"
import { Overlay, OverlayRef } from '@angular/cdk/overlay';
import { CdkPortal } from '@angular/cdk/portal';

@Component({
  selector: 'wi-show-routine',
  templateUrl: './show-routine.component.html',
  styleUrls: ['./show-routine.component.scss']
})
export class ShowRoutineComponent implements OnInit {
  moreIcon = faEllipsisV
  playCircleIcon = faPlayCircle
  dragIcon = faGripLines
  closeIcon = faTimes
  updateRef: OverlayRef
  @ViewChild(CdkPortal)
  updatePortal!: CdkPortal
  trainings = [
    {
      name: 'Workout Name',
      trainerId: 'Pepe Ramírez',
      categories: [{ value: 'Tag 1' }, { value: 'Tag 2' }],
    },
    {
      name: 'Workout Name',
      trainerId: 'Pepe Ramírez',
      categories: [{ value: 'Tag 1' }, { value: 'Tag 2' }],
    },
    {
      name: 'Workout Name',
      trainerId: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Fusce vel elit eget nunc blandit malesuada at vel ex. Suspendisse volutpat libero et ex elementum ornare. Nullam odio turpis, posuere nec erat eu, congue faucibus sem. Duis convallis pharetra turpis, id vulputate ante vehicula sed. Proin congue posuere lorem, aliquam auctor neque rutrum at. Nulla nec dignissim sapien. In vitae turpis eget erat sodales lacinia sed eu leo. Vivamus efficitur in risus in dictum. Aenean felis mauris, molestie et justo id, auctor iaculis magna. Aliquam non nulla ac leo viverra ultrices at nec erat. Nunc sit amet velit et urna blandit tempus nec vel diam. Suspendisse massa purus, vestibulum ut mattis sed, consectetur et velit. Curabitur egestas bibendum metus, sed posuere felis aliquam sed. Mauris vel dui sapien. Quisque tincidunt semper egestas.',
      categories: [{ value: 'Tag 1' }, { value: 'Tag 2' }],
    },
  ]

  constructor(overlay: Overlay) {
    this.updateRef = overlay.create({
      positionStrategy: overlay.position().global().centerHorizontally().centerVertically(),
      hasBackdrop: true,
      width: '30%'
    })
  }

  ngOnInit(): void {
  }

  drop(event: any) {
    moveItemInArray(this.trainings, event.previousIndex, event.currentIndex)
  }

  showUpdateModal() {
    this.updateRef.attach(this.updatePortal)
  }
}
