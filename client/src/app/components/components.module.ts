import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LayoutComponent } from './layout/layout.component';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TagInputComponent } from './tag-input/tag-input.component';
import { ReactiveFormsModule } from '@angular/forms';
import { FileInputComponent } from './file-input/file-input.component';

@NgModule({
  declarations: [
    LayoutComponent,
    TagInputComponent,
    FileInputComponent,
  ],
  exports: [
    LayoutComponent,
    TagInputComponent,
    FileInputComponent,
  ],
  imports: [
    CommonModule,
    FontAwesomeModule,
    ReactiveFormsModule
  ]
})
export class ComponentsModule { }
