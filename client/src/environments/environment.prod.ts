import { AuthInterceptor } from "src/app/services/auth.interceptor";
import { SynchronizedIdentityProviderService } from "src/app/services/synchronized-identity-provider.service";

export const environment = {
  production: true,
  socialApiUrl: '/api/social',
  fitnessApiUrl: '/api/fitness',
  authApiUrl: '/api',
  socialStreamingApiUrl: 'ws://localhost:5000',
  authInterceptor: AuthInterceptor,
  identityProviderService: SynchronizedIdentityProviderService
};
