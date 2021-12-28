import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SocialRoutingModule } from './social-routing.module';
import { SocialComponentsModule } from './social-components/social-components.module';

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    SocialRoutingModule,
    SocialComponentsModule
  ]
})
export class SocialModule { }
