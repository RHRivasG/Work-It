import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { ComponentsModule } from './components/components.module';
import { FitnessModule } from './fitness/fitness.module';
import { SocialModule } from './social/social.module';

@NgModule({
  declarations: [
    AppComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ComponentsModule,
    FitnessModule,
    SocialModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
