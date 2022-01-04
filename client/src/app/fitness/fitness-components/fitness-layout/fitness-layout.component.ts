import { animate, group, query, style, transition, trigger } from '@angular/animations';
import { Component, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'wi-layout',
  template: `
    <wi-fitness-layout>
      <router-outlet></router-outlet>
    </wi-fitness-layout>
  `,
  animations: []
})
export class FitnessLayoutComponent implements OnInit {

  constructor() { }

  ngOnInit(): void {
  }

}
