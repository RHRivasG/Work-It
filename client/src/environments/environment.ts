// This file can be replaced during build by using the `fileReplacements` array.
// `ng build` replaces `environment.ts` with `environment.prod.ts`.

import { FixedIdentityProviderService } from "src/app/services/fixed-identity-provider.service";

// The list of file replacements can be found in `angular.json`.
const fixedAuthBuilder = new FixedIdentityProviderService("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI2YTQ5N2IyMC01Y2MyLTQ3MDgtOGU1NC03YjRlZGYyYmQ0NDkiLCJyb2xlIjpbInBhcnRpY2lwYW50Il19.miIE1xqoKtb7ESAXCPa-x-wSHV4OkBaz30FWCBIwCjAMtOg6DYERBehvPX6zdNdOmqIrbHPKVSp_euqDXyr04Q")

export const environment = {
  production: false,
  socialApiUrl: 'http://localhost:5000',
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
