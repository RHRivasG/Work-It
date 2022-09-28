import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SocialRoutingModule } from './social-routing.module';
import { SocialComponentsModule } from './social-components/social-components.module';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { ForbiddenInterceptor } from '../services/forbidden.interceptor';
import { AddCookiesInterceptor } from '../services/add-cookies.interceptor';

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    SocialRoutingModule,
    SocialComponentsModule
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      multi: true,
      useClass: AddCookiesInterceptor
    },
    {
      provide: HTTP_INTERCEPTORS,
      multi: true,
      useClass: ForbiddenInterceptor
    },
  ]
})
export class SocialModule { }
