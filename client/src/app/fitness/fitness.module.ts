import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { FitnessRoutingModule } from './fitness-routing.module';
import { FitnessComponentsModule } from './fitness-components/fitness-components.module';

@NgModule({
  declarations: [],
  imports: [CommonModule, FitnessRoutingModule, FitnessComponentsModule],
})
export class FitnessModule {}
