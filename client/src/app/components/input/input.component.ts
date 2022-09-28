import { BreakpointObserver } from '@angular/cdk/layout';
import { Component, Input, OnInit } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';
import { faUser, IconDefinition } from '@fortawesome/free-solid-svg-icons';
import { map } from 'rxjs/operators';

@Component({
  selector: 'wi-input',
  templateUrl: './input.component.html',
  styles: [
  ],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      multi: true,
      useExisting: InputComponent
    },
  ]
})
export class InputComponent implements OnInit, ControlValueAccessor {
  @Input('placeholder')
  effectivePlaceholder = ''
  @Input()
  type = ''
  @Input()
  icon: IconDefinition = faUser
  touched = false
  value: string = ''
  onChange: (arg: string) => void = () => {}
  onTouched = () => {}

  constructor(private breakpointObserver: BreakpointObserver) { }

  writeValue(obj: any): void {
    this.value = obj
  }
  registerOnChange(fn: any): void {
    this.onChange = fn
  }
  registerOnTouched(fn: any): void {
    this.onTouched = fn
  }

  get placeholder() {
    return this.breakpointObserver.observe(['(max-width: 1023px)'])
    .pipe(
      map(state => state.matches ? this.effectivePlaceholder : '')
    )
  }

  ngOnInit(): void {
  }

  inputChanged(e: Event) {
    let input = e.target as HTMLInputElement
    this.value = input.value
    this.onChange(this.value)
    this.markAsTouched()
  }

  markAsTouched() {
    if (!this.touched) {
      this.touched = true
      this.onTouched()
    }
  }
}
