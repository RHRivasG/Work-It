import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NewTrainingComponent } from './new-training/new-training.component';
import { ComponentsModule } from 'src/app/components/components.module';
import { TrainingIndexComponent } from './training-index/training-index.component';
import { TrainingCardComponent } from './training-card/training-card.component';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { RoutineIndexComponent } from './routine-index/routine-index.component';
import { ShowRoutineComponent } from './show-routine/show-routine.component';
import { RoutineModalComponent } from './routine-modal/routine-modal.component';
import { ReactiveFormsModule } from '@angular/forms';
import { DragDropModule } from '@angular/cdk/drag-drop';
import { FullscreenOverlayContainer, OverlayContainer, OverlayModule } from '@angular/cdk/overlay';
import { PortalModule } from '@angular/cdk/portal';
import { RoutineUpdateModalComponent } from './routine-update-modal/routine-update-modal.component';



@NgModule({
  declarations: [
    NewTrainingComponent,
    TrainingIndexComponent,
    TrainingCardComponent,
    ShowRoutineComponent,
    RoutineIndexComponent,
    RoutineModalComponent,
    RoutineUpdateModalComponent
  ],
  exports: [
    NewTrainingComponent
  ],
  imports: [
    CommonModule,
    ComponentsModule,
    FontAwesomeModule,
    ReactiveFormsModule,
    PortalModule,
    OverlayModule,
    DragDropModule
  ],
  providers: [
    {
      provide: OverlayContainer,
      useClass: FullscreenOverlayContainer
    }
  ]
})
export class FitnessComponentsModule { }
