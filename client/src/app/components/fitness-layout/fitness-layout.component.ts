import { animate, keyframes, state, style, transition, trigger } from '@angular/animations';
import { Component, Inject, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { faDumbbell, faHome, faSearch, faSignOutAlt, faUser } from '@fortawesome/free-solid-svg-icons';
import { IdentityProvider, WI_IDENTITY_PROVIDER } from 'src/app/services/identity-provider';

@Component({
  selector: 'wi-fitness-layout',
  templateUrl: './fitness-layout.component.html',
  styles: [`
  .header {
    @apply bg-primary px-8 grid grid-cols-6 md:flex md:flex-row items-center shadow-xl gap-8 py-3;
  }
  .logo {
    @apply md:w-1/6 text-2xl font-semibold text-white;
  }
  .logo img {
    min-width: 50px;
  }
  .logo-link {
    @apply md:w-full flex flex-row items-center justify-evenly;
  }
  .search-bar {
    @apply relative flex-auto flex flex-row items-center border-2 border-gray-500 rounded-2xl transition duration-200 focus-within:bg-white focus-within:bg-opacity-25 focus-within:border-white;
  }
  .search-bar.hidden {
    display: none !important;
  }
  .search-bar-input {
    @apply w-full p-2 border-0 border-transparent text-white rounded-2xl bg-transparent focus:outline-none;
  }
  .search-bar-icon {
    @apply p-4 absolute top-0 right-0 h-full flex flex-row items-center text-gray-500;
  }
  .search-bar:focus-within .search-bar-icon {
    color: white;
  }
  .navigation {
    @apply md:w-1/6 flex flex-row items-center justify-evenly gap-4;
  }
  .navigation-icon {
    @apply text-gray-500 text-xl h-full;
  }
  .navigation-icon.active {
    color: white !important;
  }
  `]
})
export class LayoutComponent implements OnInit {
  id!: string
  activatedRoute = {
    home: false,
    profile: false,
    trainings: false,
    layout: false
  }
  searchIcon = faSearch
  homeIcon = faHome
  userIcon = faUser
  trainingsIcon = faDumbbell
  logoutIcon = faSignOutAlt


  constructor(router: Router, @Inject(WI_IDENTITY_PROVIDER) identityProvider: IdentityProvider) {
    const url = router.url

    identityProvider.identity.subscribe(id => {
      this.id = id
      this.activatedRoute.trainings = url.includes("fitness") && url.includes("trainings") && url.includes(id)
      this.activatedRoute.home = url.includes("trainings") && !this.activatedRoute.trainings
      this.activatedRoute.profile = url.includes("profile") || url.includes("routines")
      this.activatedRoute.layout = url.endsWith("routines") || url.endsWith("routines/") || url.endsWith("trainings/") || url.endsWith("trainings")
    })
  }

  ngOnInit(): void {
  }

  logout() {
  }
}
