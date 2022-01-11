// This file can be replaced during build by using the `fileReplacements` array.
// `ng build` replaces `environment.ts` with `environment.prod.ts`.

import { FixedIdentityProviderBuilder } from "src/app/services/fixed-identity-provider.builder";

// The list of file replacements can be found in `angular.json`.
const token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwicm9sZXMiOlsidHJhaW5lciIsInBhcnRpY2lwYW50Il19.7sFNLPOsiMEVaQc456QocJjhMu_fC5jl_R2Gb1b2uFm5-yS8D7dW4DeLgiDVcB4-WPtSHXHEJ96zo2400DaOIQ",
  fixedAuthBuilder = new FixedIdentityProviderBuilder(token)

export const environment = {
  production: false,
  socialApiUrl: 'http://localhost:5000',
  socialStreamingApiUrl: 'ws://localhost:5000',
  fitnessApiUrl: 'http://localhost:8080',
  authInterceptor: fixedAuthBuilder.authProvider,
  identityProviderService: fixedAuthBuilder.identityProvider
};

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/plugins/zone-error';  // Included with Angular CLI.
