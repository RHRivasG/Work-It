import { AuthInterceptor } from 'src/app/services/auth.interceptor';
import { SynchronizedIdentityProviderService } from 'src/app/services/synchronized-identity-provider.service';

export const environment = {
  production: true,
  socialApiUrl: 'http://localhost/api/social',
  trainingApiUrl: 'http://localhost/api/trainings/trainings',
  routineApiUrl: 'http://localhost/api/routines/routines',
  summaryApiUrl: 'http://localhost/api/summary',
  authApiUrl: 'http://localhost/api/auth',
  logoutApiUrl: 'http://localhost/api/logout',
  reportsApiUrl: 'http://localhost/api/reports',
  reportsStreamingApiUrl: 'ws://localhost:3500',
  socialStreamingApiUrl: 'ws://localhost:5000',
  authInterceptor: AuthInterceptor,
  identityProviderService: SynchronizedIdentityProviderService,
};
