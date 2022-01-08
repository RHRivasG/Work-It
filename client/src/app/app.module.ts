import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { ComponentsModule } from './components/components.module';
import { FitnessModule } from './fitness/fitness.module';
import { SocialModule } from './social/social.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { BrowserModule } from '@angular/platform-browser';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { WI_IDENTITY_PROVIDER } from './services/identity-provider';
import { environment } from 'src/environments/environment';

@NgModule({
  declarations: [
    AppComponent,
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    ComponentsModule,
    FitnessModule,
    SocialModule,
    HttpClientModule
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      multi: true,
      useClass: environment.authInterceptor
    },
    {
      provide: WI_IDENTITY_PROVIDER ,
      useClass: environment.identityProviderService
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
