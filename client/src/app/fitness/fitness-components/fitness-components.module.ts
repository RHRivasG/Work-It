import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NewTrainingComponent } from './new-training/new-training.component';
import { ComponentsModule } from 'src/app/components/components.module';



@NgModule({
  declarations: [
    NewTrainingComponent
  ],
  exports: [
    NewTrainingComponent
  ],
  imports: [
    CommonModule,
    ComponentsModule
  ]
})
export class FitnessComponentsModule { }
