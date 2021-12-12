import { Component, OnInit } from '@angular/core';
import { faDumbbell, faHome, faSearch, faSignOutAlt, faUser } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'wi-layout',
  templateUrl: './layout.component.html',
  styles: [`
  .header {
    @apply bg-primary px-8 flex flex-row items-center shadow-xl gap-8;
  }
  .logo {
    @apply w-1/6 text-2xl font-semibold text-white;
  }
  .logo-link {
    @apply flex flex-row w-full items-center justify-evenly;
  }
  .search-bar {
    @apply w-2/3 relative flex flex-row items-center border-2 border-gray-500 rounded-2xl transition duration-200 focus-within:bg-white focus-within:bg-opacity-25 focus-within:border-white;
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
    @apply w-1/6 flex flex-row items-center justify-evenly gap-4;
  }
  .navigation-icon {
    @apply text-gray-500 text-xl h-full;
  }
  `]
})
export class LayoutComponent implements OnInit {
  searchIcon = faSearch
  homeIcon = faHome
  userIcon = faUser
  trainingsIcon = faDumbbell
  logoutIcon = faSignOutAlt

  constructor() { }

  ngOnInit(): void {
  }

}
