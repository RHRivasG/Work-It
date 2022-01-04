import { AuthInterceptor } from "src/app/services/auth.interceptor";
import { SynchronizedIdentityProviderService } from "src/app/services/synchronized-identity-provider.service";

export const environment = {
  production: true,
  socialApiUrl: 'http://localhost:3000/api/social',
  authInterceptor: AuthInterceptor,
  identityProviderService: SynchronizedIdentityProviderService
};
