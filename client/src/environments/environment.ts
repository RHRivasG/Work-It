// This file can be replaced during build by using the `fileReplacements` array.
// `ng build` replaces `environment.ts` with `environment.prod.ts`.

import { AuthInterceptor } from 'src/app/services/auth.interceptor';
import { FixedIdentityProviderBuilder } from 'src/app/services/fixed-identity-provider.builder';
import { SynchronizedIdentityProviderService } from 'src/app/services/synchronized-identity-provider.service';

// The list of file replacements can be found in `angular.json`.
const token =
    'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIwYmQzMGE3NS1kOTlmLTRkMGYtYTY5Ni02MDAxYzExYjc3MmYiLCJyb2xlcyI6WyJwYXJ0aWNpcGFudCIsInRyYWluZXIiXX0.g8YqXQeOinYrl1h9jUp0KSagMsBJkia1ucRq_YHIy9YxzwHsbhPsJTMHOxlaU4jjuM-DTlPJpILnPIvUPmSM5g',
  fixedAuthBuilder = new FixedIdentityProviderBuilder(token);

export const environment = {
  production: false,
  socialApiUrl: 'http://localhost:3000/api/social',
  reportsApiUrl: 'http://localhost:3000/api/reports',
  authApiUrl: 'http://localhost:3000',
  reportsStreamingApiUrl: 'ws://localhost:3500',
  socialStreamingApiUrl: 'ws://localhost:5000',
  fitnessApiUrl: 'http://localhost:3000/api/fitness',
  summaryApiUrl: 'http://localhost:3000/api/summary',
  authInterceptor: AuthInterceptor,
  identityProviderService: SynchronizedIdentityProviderService,
};

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/plugins/zone-error';  // Included with Angular CLI.
