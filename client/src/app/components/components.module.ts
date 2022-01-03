import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LayoutComponent as FitnessLayoutComponent } from './fitness-layout/fitness-layout.component';
import { LayoutComponent as SocialLayoutComponent } from './social-layout/social-layout.component';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TagInputComponent } from './tag-input/tag-input.component';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { FileInputComponent } from './file-input/file-input.component';
import { PasswordInputComponent } from './password-input/password-input.component';
import { SubmitButtonComponent } from './submit-button/submit-button.component';
import { InputComponent } from './input/input.component';

@NgModule({
  declarations: [
    FitnessLayoutComponent,
    SocialLayoutComponent,
    TagInputComponent,
    FileInputComponent,
    PasswordInputComponent,
    SubmitButtonComponent,
    InputComponent,
  ],
  exports: [
    FitnessLayoutComponent,
    SocialLayoutComponent,
    TagInputComponent,
    FileInputComponent,
    PasswordInputComponent,
    SubmitButtonComponent,
    InputComponent
  ],
  imports: [
    CommonModule,
    FontAwesomeModule,
    ReactiveFormsModule,
    RouterModule
  ]
})
export class ComponentsModule { }
