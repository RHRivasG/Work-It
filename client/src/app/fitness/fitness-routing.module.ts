import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PreferencesResolver } from '../services/preferences.resolver';
import { ProfileResolver } from '../social/services/profile.resolver';
import { FitnessLayoutComponent } from './fitness-components/fitness-layout/fitness-layout.component';
import { NewTrainingComponent } from './fitness-components/new-training/new-training.component';
import { RoutineIndexComponent } from './fitness-components/routine-index/routine-index.component';
import { RoutinePlayerComponent } from './fitness-components/routine-player/routine-player.component';
import { ShowRoutineComponent } from './fitness-components/show-routine/show-routine.component';
import { SummaryModalComponent } from './fitness-components/summary-modal/summary-modal.component';
import { TrainerTrainingsComponent } from './fitness-components/trainer-trainings/trainer-trainings.component';
import { TrainingIndexComponent } from './fitness-components/training-index/training-index.component';
import { RoutineResolver } from './services/routine.resolver';
import { RoutinesResolver } from './services/routines.resolver';
import { SummaryResolver } from './services/summary.resolver';
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
        data: {
          myTrainings: true,
        },
        resolve: {
          training: TrainingQueryResolver,
          preferences: PreferencesResolver,
        },
      },
      {
        path: 'trainings',
        component: TrainingIndexComponent,
        data: {
          searchable: true,
          home: true,
        },
        resolve: {
          trainings: TrainingsResolver,
          routines: RoutinesResolver,
        },
      },
      {
        path: 'trainer/trainings',
        data: {
          searchable: true,
          myTrainings: true,
        },
        component: TrainerTrainingsComponent,
        resolve: {
          trainings: TrainerTrainingsResolver,
          routines: RoutinesResolver,
          profile: ProfileResolver,
        },
      },
      {
        path: 'routines',
        component: RoutineIndexComponent,
        data: {
          searchable: true,
          myProfile: true,
        },
        resolve: {
          routines: RoutinesResolver,
          profile: ProfileResolver,
        },
      },
      {
        path: 'routines/:id',
        component: ShowRoutineComponent,
        data: {
          searchable: true,
          myProfile: true,
        },
        resolve: {
          routine: RoutineResolver,
        },
      },
      {
        path: 'routines/:id/play',
        component: RoutinePlayerComponent,
        data: {
          searchable: false,
          myProfile: true,
        },
        resolve: {
          routine: RoutineResolver,
        },
      },
      {
        path: 'routines/:id/summary',
        component: SummaryModalComponent,
        data: {
          searchable: false,
          myProfile: true,
        },
        resolve: {
          routine: SummaryResolver,
        },
      },
    ],
  },
];
@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class FitnessRoutingModule {}
