import { InjectionToken } from "@angular/core";
import { Observable } from "rxjs";

export const WI_GLOBAL_SEARCH = new InjectionToken<GlobalSearch<any>>("WI_GLOBAL_SEARCH")

export type Extractor<T> = (arg0: T) => string

export interface GlobalSearch<T> {

  get result(): Observable<T[]>

  set extractor(fn: Extractor<T>)

  set dataSource(list: T[])

  searchValue: string
}
