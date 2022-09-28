import { Component, Input, OnInit } from '@angular/core';
import { faSpinner } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'wi-submit-button',
  templateUrl: './submit-button.component.html',
  styleUrls: ['./submit-button.component.scss']
})
export class SubmitButtonComponent implements OnInit {
  @Input()
  loading = false
  @Input()
  submit = ""
  @Input()
  disabled = false
  @Input()
  type!: string
  loadingIcon = faSpinner

  constructor() { }

  ngOnInit(): void {
  }

}
