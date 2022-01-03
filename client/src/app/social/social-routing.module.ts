import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { FitnessLayoutComponent } from '../fitness/fitness-components/fitness-layout/fitness-layout.component';
import { CanActivateDashboardGuard } from '../services/can-activate-dashboard.guard';
import { CanActivateLoginGuard } from '../services/can-activate-login.guard';
import { PreferencesResolver } from '../services/preferences.resolver';
import { ProfileResolver } from './services/profile.resolver';
import { ChangePasswordComponent } from './social-components/change-password/change-password.component';
import { DashboardComponent } from './social-components/dashboard/dashboard.component';
import { LayoutComponent as SocialLayoutComponent } from './social-components/layout/layout.component';
import { LoginComponent } from './social-components/login/login.component';
import { ProfileComponent } from './social-components/profile/profile.component';
import { RegisterComponent } from './social-components/register/register.component';

const routes: Routes = [
  {
    path: 'auth',
    component: SocialLayoutComponent ,
    children: [
      {
        path: 'login',
        component: LoginComponent,
        canActivate: [CanActivateLoginGuard]
      },
      {
        path: 'register',
        component: RegisterComponent
      },
      {
        path: 'change-password/:id',
        component: ChangePasswordComponent
      },
      {
        path: '**',
        redirectTo: 'login'
      },
    ]
  },
  {
    path: 'profile',
    component: FitnessLayoutComponent,
    children: [
      {
        path: ':id',
        component: ProfileComponent,
        resolve: {
          profile: ProfileResolver,
          preferences: PreferencesResolver
        },
      }
    ]
  },
  {
    path: 'dashboard',
    component: DashboardComponent,
    canActivate: [CanActivateDashboardGuard]
  },
  {
    path: '**',
    redirectTo: 'auth'
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class SocialRoutingModule { }
