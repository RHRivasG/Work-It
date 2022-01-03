import { BreakpointObserver } from '@angular/cdk/layout';
import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'wi-social-layout',
  templateUrl: './social-layout.component.html',
  styleUrls: ['./social-layout.component.scss']
})
export class LayoutComponent implements OnInit {
  mobile = false

  constructor(private breakpointObserver: BreakpointObserver) {
  }

  ngOnInit(): void {
    this.breakpointObserver.observe(["(max-width: 1023px)"])
    .subscribe(state => {
      this.mobile = state.matches
    })
  }

}
