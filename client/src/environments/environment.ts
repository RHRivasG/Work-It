// This file can be replaced during build by using the `fileReplacements` array.
// `ng build` replaces `environment.ts` with `environment.prod.ts`.

import { FixedIdentityProviderBuilder } from "src/app/services/fixed-identity-provider.builder";
import { SynchronizedIdentityProviderService } from "src/app/services/synchronized-identity-provider.service";

// The list of file replacements can be found in `angular.json`.
const token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI5NzQ3YjM0MC0xNWY0LTQ5ODItOGExOC0xMjhjN2JlMDBjN2EiLCJyb2xlcyI6WyJwYXJ0aWNpcGFudCJdfQ.cot5UlsKpLofSjXTmexILbNYiBZJZ11j1HvGmQTvzTzSaLg6pqnyZbYSoYn5R4OarPE1BUh2_SxXk-9WRhMDOw",
  fixedAuthBuilder = new FixedIdentityProviderBuilder(token)

export const environment = {
  production: false,
  socialApiUrl: 'http://localhost:5000',
  socialStreamingApiUrl: 'ws://localhost:5000',
  fitnessApiUrl: 'http://localhost:8080',
  authInterceptor: fixedAuthBuilder.authProvider,
  identityProviderService: SynchronizedIdentityProviderService
};

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/plugins/zone-error';  // Included with Angular CLI.
