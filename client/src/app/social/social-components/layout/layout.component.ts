import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'wi-layout',
  template: `
    <wi-social-layout>
      <router-outlet style="display: none;"></router-outlet>
    </wi-social-layout>
  `,
})
export class LayoutComponent implements OnInit {

  constructor() { }

  ngOnInit(): void {
  }

}
