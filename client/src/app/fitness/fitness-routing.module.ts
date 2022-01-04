import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { FitnessLayoutComponent } from './fitness-components/fitness-layout/fitness-layout.component';
import { NewTrainingComponent } from './fitness-components/new-training/new-training.component';
import { RoutineIndexComponent } from './fitness-components/routine-index/routine-index.component';
import { ShowRoutineComponent } from './fitness-components/show-routine/show-routine.component';
import { TrainingIndexComponent } from './fitness-components/training-index/training-index.component';

const routes: Routes = [
  {
    path: '',
    component: FitnessLayoutComponent,
    children: [
      {
        path: 'trainings/new',
        component: NewTrainingComponent
      },
      {
        path: 'trainings',
        component: TrainingIndexComponent,
        data: {
          animation: 'searchable'
        }
      },
      {
        path: 'routines',
        component: RoutineIndexComponent,
        data: {
          animation: 'searchable'
        }
      },
      {
        path: 'routines/:id',
        component: ShowRoutineComponent
      }
    ]

  }
]
@NgModule({
  imports: [
    RouterModule.forChild(routes),
  ],
  exports: [RouterModule]
})
export class FitnessRoutingModule { }
