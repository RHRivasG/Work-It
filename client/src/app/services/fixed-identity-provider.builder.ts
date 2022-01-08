import { HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Injectable } from '@angular/core';
import jwtDecode, { JwtPayload } from 'jwt-decode';
import { Observable, of } from 'rxjs';
import { AuthInterceptor } from './auth.interceptor';
import { IdentityProvider } from './identity-provider';

export class FixedIdentityProviderBuilder {
  private identity: string
  private token: string

  constructor(jwt: string) {
    const claims = jwtDecode<JwtPayload>(jwt)
    if (!claims.sub) throw new Error("Invalid JWT Token")
    this.identity = claims.sub
    this.token = jwt
  }

  get identityProvider() {
    const identity = this.identity
    class FixedIdentityProviderGeneratedService implements IdentityProvider {
      get identity(): Observable<string> {
        return of(identity)
      }
    }

    return FixedIdentityProviderGeneratedService
  }

  get authProvider() {
    const token = this.token

    @Injectable({ providedIn: 'root' })
    class TokenAuthInterceptor extends AuthInterceptor {
      intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
        const req = !request.url.includes("login") ? request.clone({
          setHeaders: {
            'Authorization': `Bearer ${token}`
          }
        }) : request
        return super.intercept(req, next)
      }
    }

    return TokenAuthInterceptor
  }
}
