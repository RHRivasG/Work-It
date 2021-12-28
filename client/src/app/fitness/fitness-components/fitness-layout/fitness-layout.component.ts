import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'wi-fitness-layout',
  template: `
    <wi-layout>
      <router-outlet></router-outlet>
    </wi-layout>
  `,
})
export class FitnessLayoutComponent implements OnInit {

  constructor() { }

  ngOnInit(): void {
  }

}
