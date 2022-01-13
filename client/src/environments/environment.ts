// This file can be replaced during build by using the `fileReplacements` array.
// `ng build` replaces `environment.ts` with `environment.prod.ts`.

import { FixedIdentityProviderBuilder } from "src/app/services/fixed-identity-provider.builder";
import { SynchronizedIdentityProviderService } from "src/app/services/synchronized-identity-provider.service";

// The list of file replacements can be found in `angular.json`.
const token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJjZTE3NGE4Ni03MWY4LTRlY2EtYTljZC00NTk3ZGE5NTEyZDQiLCJyb2xlcyI6WyJwYXJ0aWNpcGFudCIsInRyYWluZXIiXX0.uToTRTM5AveEDaNZ4MQmCrzU4myo30bc_e3X19LjlsUs8Za3O2_4aN23c7i56mPzsWFk78LOSW2Td2CHx3fDtA",
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
