import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'social'
  },
  {
    path: 'fitness',
    loadChildren: () => import('./fitness/fitness.module').then(m => m.FitnessModule)
  },
  {
    path: 'social',
    loadChildren: () => import('./social/social.module').then(m => m.SocialModule)
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
