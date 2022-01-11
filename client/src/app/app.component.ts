import { animate, query, style, transition, trigger } from '@angular/animations';
import { Component, HostListener } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { map, tap, toArray } from 'rxjs/operators';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  prepareAnimation(outlet: RouterOutlet) {}

  @HostListener("window:unload", [ '$event' ])
  unloadLogin() {
    localStorage.removeItem("identity")
  }

  print() {
  }
}
