import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LoginComponent } from './login/login.component';
import { ReactiveFormsModule } from '@angular/forms';
import { LayoutModule } from "@angular/cdk/layout";
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { RegisterComponent } from './register/register.component';
import { LayoutComponent } from './layout/layout.component';
import { SocialRoutingModule } from '../social-routing.module';
import { ComponentsModule } from 'src/app/components/components.module';
import { ProfileComponent } from './profile/profile.component';
import { ChangePasswordComponent } from './change-password/change-password.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { PortalModule } from '@angular/cdk/portal';
import { AdminLoginComponent } from './admin-login/admin-login.component';

@NgModule({
  declarations: [
    LoginComponent,
    RegisterComponent,
    LayoutComponent,
    ProfileComponent,
    ChangePasswordComponent,
    DashboardComponent,
    AdminLoginComponent
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FontAwesomeModule,
    SocialRoutingModule,
    ComponentsModule,
    PortalModule,
    LayoutModule
  ]
})
export class SocialComponentsModule { }
