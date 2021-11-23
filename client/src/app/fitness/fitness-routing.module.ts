import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { NewTrainingComponent } from './fitness-components/new-training/new-training.component';

const routes: Routes = [
  {
    path: 'trainings/new',
    component: NewTrainingComponent
  }
];

@NgModule({
  imports: [
    RouterModule.forChild(routes),
  ],
  exports: [RouterModule]
})
export class FitnessRoutingModule { }
