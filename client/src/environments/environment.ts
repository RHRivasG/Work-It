// This file can be replaced during build by using the `fileReplacements` array.
// `ng build` replaces `environment.ts` with `environment.prod.ts`.

import { FixedIdentityProviderService } from "src/app/services/fixed-identity-provider.service";

// The list of file replacements can be found in `angular.json`.
const fixedAuthBuilder = new FixedIdentityProviderService("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI4OWY3ZGM2OC0xYzc4LTQxMjItYmFkMS01ZTc0N2MwNjNjNTQiLCJyb2xlcyI6WyJ0cmFpbmVyIl19.UQQ6JR-VGxzKbCP6XKJI6wzpXDl2kuAIA3ybo51Vxlxy4HB620cdbNhWSmPU0yu_7VdvbvoP0HvLr9ReIWSM5A")

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
