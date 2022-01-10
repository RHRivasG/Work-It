import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PreferencesResolver } from '../services/preferences.resolver';
import { FitnessLayoutComponent } from './fitness-components/fitness-layout/fitness-layout.component';
import { NewTrainingComponent } from './fitness-components/new-training/new-training.component';
import { RoutineIndexComponent } from './fitness-components/routine-index/routine-index.component';
import { ShowRoutineComponent } from './fitness-components/show-routine/show-routine.component';
import { TrainerTrainingsComponent } from './fitness-components/trainer-trainings/trainer-trainings.component';
import { TrainingIndexComponent } from './fitness-components/training-index/training-index.component';
import { RoutineResolver } from './services/routine.resolver';
import { RoutinesResolver } from './services/routines.resolver';
import { TrainerTrainingsResolver } from './services/trainer-training.resolver';
import { TrainingQueryResolver } from './services/training-query.resolver';
import { TrainingsResolver } from './services/trainings.resolver';

const routes: Routes = [
  {
    path: '',
    component: FitnessLayoutComponent,
    children: [
      {
        path: 'trainings/new',
        component: NewTrainingComponent,
        resolve: {
          training: TrainingQueryResolver,
          // preferences: PreferencesResolver
        }
      },
      {
        path: 'trainings',
        component: TrainingIndexComponent,
        data: {
          animation: 'searchable'
        },
        resolve: {
          trainings: TrainingsResolver,
          // routines: RoutinesResolver
        }
      },
      {
        path:'trainer/trainings',
        data: {
          animation: 'searchable'
        },
        component: TrainerTrainingsComponent,
        resolve: {
          trainings: TrainerTrainingsResolver,
          routines: RoutinesResolver
        }
      },
      {
        path: 'routines',
        component: RoutineIndexComponent,
        data: {
          animation: 'searchable'
        },
        resolve: {
          routines: RoutinesResolver
        }
      },
      {
        path: 'routines/:id',
        component: ShowRoutineComponent,
        resolve: {
          routine: RoutineResolver
        }
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
