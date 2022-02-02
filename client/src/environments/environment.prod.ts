import { AuthInterceptor } from "src/app/services/auth.interceptor";
import { SynchronizedIdentityProviderService } from "src/app/services/synchronized-identity-provider.service";

export const environment = {
  production: true,
  socialApiUrl: 'http://localhost/api/social',
  fitnessApiUrl: 'http://localhost/api/fitness',
  authApiUrl: 'http://localhost/api',
  socialStreamingApiUrl: 'ws://localhost:5000',
  authInterceptor: AuthInterceptor,
  identityProviderService: SynchronizedIdentityProviderService
};
