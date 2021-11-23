import { AfterViewInit, Component, ElementRef, Input, OnDestroy, OnInit, ViewChild, ViewChildren } from '@angular/core';
import { FormBuilder, FormGroup, ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';
import { faTimes } from '@fortawesome/free-solid-svg-icons';
import { fromEvent, merge, Subscription } from 'rxjs';
import { mapTo } from 'rxjs/operators';

@Component({
  selector: 'wi-tag-input',
  templateUrl: './tag-input.component.html',
  styleUrls: ['./tag-input.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      multi: true,
      useExisting: TagInputComponent
    }
  ]
})
export class TagInputComponent implements ControlValueAccessor, AfterViewInit, OnDestroy, OnInit {
  closeIcon = faTimes
  tagList: string[] = []
  tagInputForm: FormGroup
  updatingTag?: string
  onChange: (_: string[]) => void = () => { }
  onTouched: () => void = () => { }
  touched = true
  disabled = false
  focused = false

  @ViewChild('input') inputRef!: ElementRef<HTMLInputElement>
  @ViewChild('inputControl') inputControlRef!: ElementRef<HTMLInputElement>
  @Input() allTagsList!: string[]

  get validTagList() {
    return this.allTagsList.filter(tag => !this.tagList.includes(tag))
  }

  private subscription!: Subscription

  constructor(fBuilder: FormBuilder) {
    this.tagInputForm = fBuilder.group({
      tag: ['']
    })
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe()
  }

  ngAfterViewInit(): void {
    this.subscription = merge(
      fromEvent(this.inputRef.nativeElement, 'focusin').pipe(mapTo(true)),
      fromEvent(this.inputRef.nativeElement, 'focusout').pipe(mapTo(false)),
      fromEvent(document.querySelectorAll('fa-icon')!, 'focus').pipe(mapTo(false))
    ).subscribe(val => {
      if (val && this.inputRef.nativeElement.matches(':focus-within')) {
        this.focused = true
        this.markAsTouched()
      }
      else if (!val && !this.inputRef.nativeElement.matches(':focus-within')) {
        this.focused = false
      }
    })
  }

  writeValue(val: string | string[]): void {
    if (Array.isArray(val)) this.tagList.concat(val)
    else this.tagList.push(val)
  }

  registerOnChange(fn: any): void {
    this.onChange = fn
  }

  registerOnTouched(fn: any): void {
    this.onTouched = fn
  }

  ngOnInit(): void {
  }

  setFocus() {
    console.log('Focusing')
    this.inputControlRef.nativeElement.focus()
  }

  addTagFromAllList(e: Event, tag: string) {
    e.stopPropagation()
    e.preventDefault()

    this.inputControlRef.nativeElement.focus()
    this.addTag(tag)
  }

  addTagFromInput(e: Event) {
    e.stopPropagation()
    e.preventDefault()

    this.addTag()
  }

  removeLastTagFromInputWhenInputEmpty() {
    if (this.tagInputForm.controls['tag'].value == '')
      this.removeTag(this.tagList[this.tagList.length - 1])
  }

  removeTagFromInput(e: Event, tag: string) {
    e.stopPropagation()
    e.preventDefault()

    this.removeTag(tag)
  }

  toggleUpdating(tag: string) {
    if (tag == this.updatingTag) {
      this.updatingTag = undefined
      this.resetTagInput()
      return
    }

    this.updatingTag = tag
    this.tagInputForm.controls['tag'].setValue(this.updatingTag)
  }

  addTag(tag?: string) {
    const tagValue = tag || this.tagInputForm.controls['tag'].value
    this.resetTagInput()

    if (tagValue == '') return

    if (this.updatingTag) {
      const uIndex = this.tagList.indexOf(this.updatingTag)
      this.tagList.splice(uIndex, 1, tagValue)
      this.updatingTag = undefined
    }

    if (!this.tagList.includes(tagValue)) {
      this.tagList.push(tagValue)
      // this.tagList.sort()
    }

    this.onChange(this.tagList)
    this.markAsTouched()
  }

  removeTag(tag: string) {
    const index = this.tagList.indexOf(tag)
    if (index == -1) return
    if (this.updatingTag == tag) this.updatingTag = undefined
    this.tagList.splice(index, 1)

    this.onChange(this.tagList)
    this.markAsTouched()
  }

  setDisabledState(disabled: boolean) {
    this.disabled = disabled

    if (disabled) {
      this.tagInputForm.controls['tag'].disable()
    } else {
      this.tagInputForm.controls['tag'].enable()
    }
  }

  private markAsTouched() {
    if (!this.touched) {
      this.touched = true
      this.onTouched()
    }
  }

  private resetTagInput() {
    this.tagInputForm.controls['tag'].setValue('')
  }

}
