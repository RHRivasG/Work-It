import { AfterViewInit, Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { faSpinner, faUpload } from '@fortawesome/free-solid-svg-icons';
import { fromEvent, Subscription } from 'rxjs';
import { filter, map, switchMap } from 'rxjs/operators';
import pretty_bytes from 'pretty-bytes';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';

export type FileInputValue = {
  name: string,
  value: string,
  ext: string,
  size: number
}

@Component({
  selector: 'wi-file-input',
  templateUrl: './file-input.component.html',
  styleUrls: ['./file-input.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      multi: true,
      useExisting: FileInputComponent
    }
  ]
})
export class FileInputComponent implements OnInit, OnDestroy, AfterViewInit, ControlValueAccessor {
  uploadIcon = faUpload
  loadingIcon = faSpinner
  subscription!: Subscription
  value: FileInputValue = {
    name: '',
    value: '',
    ext: '',
    size: 0
  }
  onChange = (_: FileInputValue) => { }
  onTouched = () => { }
  touched = false
  loading = false

  get fileInfo(): string {
    return `${this.value.name} ${this.value.size == 0 ? '' : '(' + pretty_bytes(this.value.size) + ')'}`
  }

  @ViewChild('fileInput') inputRef!: ElementRef<HTMLInputElement>

  constructor() { }

  ngOnDestroy(): void {
    this.subscription.unsubscribe()
  }

  writeValue(val: any): void {
    if (val.name && val.value && val.ext && val.size) this.value = val
  }

  registerOnChange(fn: any): void {
    this.onChange = fn
  }

  registerOnTouched(fn: any): void {
    this.onTouched = fn
  }

  ngOnInit(): void {
  }

  ngAfterViewInit(): void {
    this.subscription =
      fromEvent(this.inputRef.nativeElement, 'change')
        .pipe(
          map(e => (<HTMLInputElement>e.target).files),
          filter(files => files != null && files.length > 0),
          switchMap(files => {
            const file = files![0],
              reader = new FileReader()

            reader.readAsDataURL(file)

            this.loading = true

            return fromEvent(reader, 'load').pipe(
              map(() => ({
                result: reader.result as string,
                name: file.name,
                size: file.size
              }))
            )
          })
        )
        .subscribe(({ name, result, size }) => {
          const split = name.split('.'),
            ext = split[split.length - 1]

          this.value = {
            name,
            value: result,
            ext: ext,
            size
          }

          this.onChange(this.value)
          if (!this.touched) {
            this.touched = true
            this.onTouched()
          }
          this.loading = false
        })
  }

  openFileDialog() {
    if (this.inputRef) {
      const input = this.inputRef.nativeElement
      input.click()
    }
  }

}
