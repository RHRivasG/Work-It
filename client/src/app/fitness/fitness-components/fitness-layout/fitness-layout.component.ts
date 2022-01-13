import { animate, group, query, style, transition, trigger } from '@angular/animations';
import { AfterViewChecked, AfterViewInit, ChangeDetectorRef, Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { filter, map } from 'rxjs/operators';

@Component({
  selector: 'wi-layout',
  template: `
    <wi-fitness-layout
      [home]="home"
      [trainings]="trainings"
      [profile]="profile"
      [searchable]="searchable">
      <router-outlet #outlet></router-outlet>
    </wi-fitness-layout>
  `,
  animations: []
})
export class FitnessLayoutComponent implements OnInit, AfterViewInit {
  searchable = false
  profile = false
  home = false
  trainings = false
  @ViewChild(RouterOutlet) outlet!: RouterOutlet

  constructor(private router: Router, private activatedRoute: ActivatedRoute) {
        this.router
        .events.pipe(
          filter(event => event instanceof NavigationEnd),
          map(() => {
            let child = this.activatedRoute.firstChild;
            while (child) {
              if (child.firstChild) {
                child = child.firstChild;
              } else if (child.snapshot.data && child.snapshot.data) {
                return child.snapshot.data;
              } else {
                return null;
              }
            }
            return null;
          })
        ).subscribe((customData: any) => {
          this.searchable = customData.searchable
          this.profile = customData.myProfile
          this.trainings = customData.myTrainings
          this.home = customData.home
        });
  }

  ngAfterViewInit(): void {
  }

  ngOnInit(): void {
  }

}
