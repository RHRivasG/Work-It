import { BreakpointObserver } from '@angular/cdk/layout';
import { Component, Input, OnInit } from '@angular/core';
import { AbstractControl, ControlValueAccessor, NG_VALIDATORS, NG_VALUE_ACCESSOR, ValidationErrors, Validator, Validators } from '@angular/forms';
import { faLock } from '@fortawesome/free-solid-svg-icons';
import { map, tap } from 'rxjs/operators';

const LOGIN_FORMAT = [
  /[a-z]+/,
  /[A-Z]+/,
  /[0-9]+/,
  /[-.,;:/\\{}\[\]()@&^*%$#!?]+/
]

@Component({
  selector: 'wi-password-input',
  templateUrl: './password-input.component.html',
  styles: [``],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      multi: true,
      useExisting: PasswordInputComponent
    },
    {
      provide: NG_VALIDATORS,
      multi: true,
      useExisting: PasswordInputComponent
    }
  ]
})
export class PasswordInputComponent implements ControlValueAccessor, OnInit, Validator {
  lockIcon = faLock
  password: string = ''
  touched = false
  onChange: (arg: string) => void = () => {}
  onTouched = () => {}

  get placeholder() {
    return this.breakpointObserver.observe(["(max-width: 1023px)"])
    .pipe(
      map(state => state.matches ? 'Password' : '')
    )
  }

  constructor(private breakpointObserver: BreakpointObserver) {
  }

  validate(control: AbstractControl): ValidationErrors | null {
    const matches = LOGIN_FORMAT.map(r => this.password.match(r)).every(match => !!match)
    if (!matches || this.password.length <= 8) return { badFormat: true }
    else return null
  }

  writeValue(val: any): void {
    this.password = val
  }

  registerOnChange(fn: any): void {
    this.onChange = fn
  }

  registerOnTouched(fn: any): void {
    this.onTouched = fn
  }

  ngOnInit(): void {
  }

  inputChanged(e: Event) {
    let input = e.target as HTMLInputElement
    this.password = input.value
    this.onChange(this.password)
    this.markAsTouched()
  }

  markAsTouched() {
    if (!this.touched) {
      this.touched = true
      this.onTouched()
    }
  }

}
