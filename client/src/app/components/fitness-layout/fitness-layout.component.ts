import { animate, keyframes, state, style, transition, trigger } from '@angular/animations';
import { Overlay, OverlayRef } from '@angular/cdk/overlay';
import { TemplatePortal } from '@angular/cdk/portal';
import { Component, Inject, InjectionToken, Input, OnInit, TemplateRef, ViewContainerRef } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { faDumbbell, faHome, faSearch, faSignOutAlt, faTimes, faUser } from '@fortawesome/free-solid-svg-icons';
import { GlobalSearch, WI_GLOBAL_SEARCH } from 'src/app/services/global-search';
import { GlobalSearchService } from 'src/app/services/global-search.service';
import { IdentityProvider, WI_IDENTITY_PROVIDER } from 'src/app/services/identity-provider';
import { LogoutService } from 'src/app/services/logout.service';

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
  `],
  providers: [
    {
      provide: WI_GLOBAL_SEARCH,
      useValue: new GlobalSearchService()
    }
  ]
})
export class LayoutComponent implements OnInit {
  id!: string
  @Input() searchable!: boolean
  @Input() home!: boolean
  @Input() profile!: boolean
  @Input() trainings!: boolean
  closeIcon = faTimes
  searchIcon = faSearch
  homeIcon = faHome
  userIcon = faUser
  trainingsIcon = faDumbbell
  logoutIcon = faSignOutAlt
  userChoice: OverlayRef


  constructor(
    router: Router,
    activatedRoute: ActivatedRoute,
    private logoutService: LogoutService,
    overlay: Overlay,
    private container: ViewContainerRef,
    @Inject(WI_IDENTITY_PROVIDER) identityProvider: IdentityProvider,
    @Inject(WI_GLOBAL_SEARCH) public searchService: GlobalSearch<unknown>
  ) {
    identityProvider.identity.subscribe(id => this.id = id)
    this.userChoice = overlay.create({
      positionStrategy: overlay.position().global().centerHorizontally().centerVertically(),
      hasBackdrop: true,
      width: '25%',
    })
  }

  openUserChoice(choice: TemplateRef<unknown>) {
    this.userChoice.attach(new TemplatePortal(choice, this.container))
  }

  closeChoice() {
    this.userChoice.detach()
  }

  search(event: Event) {
    const input = event.target as HTMLInputElement
    this.searchService.searchValue = input.value
  }

  ngOnInit(): void {
  }

  logout() {
    this.logoutService.logout()
  }
}
