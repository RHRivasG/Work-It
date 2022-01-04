import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SocialRoutingModule } from './social-routing.module';
import { SocialComponentsModule } from './social-components/social-components.module';
import { HttpClient, HTTP_INTERCEPTORS } from '@angular/common/http';
import { AuthInterceptor } from '../services/auth.interceptor';
import { ForbiddenInterceptor } from '../services/forbidden.interceptor';
import { IdentityProvider, WI_IDENTITY_PROVIDER } from '../services/identity-provider';
import { environment } from 'src/environments/environment';
import { SynchronizedIdentityProviderService } from '../services/synchronized-identity-provider.service';
import { identity } from 'rxjs';
import { IdentityStorageService } from '../services/identity-storage.service';
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
    {
      provide: HTTP_INTERCEPTORS,
      multi: true,
      useClass: environment.authInterceptor
    },
    {
      provide: WI_IDENTITY_PROVIDER ,
      useClass: environment.identityProviderService
    }
  ]
})
export class SocialModule { }
