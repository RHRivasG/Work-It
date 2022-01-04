import { InjectionToken } from "@angular/core";
import { Observable } from "rxjs";

export const WI_IDENTITY_PROVIDER = new InjectionToken<IdentityProvider>("WI_IDENTITY_PROVIDER")

export interface IdentityProvider {
  get identity(): Observable<string>
}
