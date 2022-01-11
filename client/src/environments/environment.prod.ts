import { AuthInterceptor } from "src/app/services/auth.interceptor";
import { SynchronizedIdentityProviderService } from "src/app/services/synchronized-identity-provider.service";

export const environment = {
  production: true,
  socialApiUrl: 'http://localhost:3000/api/social',
  fitnessApiUrl: 'http://localhost:3000/api/fitness',
  socialStreamingApiUrl: 'ws://localhost:5000',
  authInterceptor: AuthInterceptor,
  identityProviderService: SynchronizedIdentityProviderService
};
