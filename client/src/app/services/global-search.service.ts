import { Injectable } from '@angular/core';
import { Observable, ReplaySubject, Subject } from 'rxjs';
import { Extractor, GlobalSearch } from './global-search';

@Injectable()
export class GlobalSearchService<T> implements GlobalSearch<T> {
  _value: string = ""
  _searchSubject: Subject<T[]> = new ReplaySubject()
  _ds: T[] = []
  _extractor: Extractor<T> = () => ""

  constructor() {
    this.fireSearch()
  }

  set searchValue(value: string) {
    this._value = value
    this.fireSearch()
  }

  get searchValue() {
    return this._value
  }

  get result(): Observable<T[]> {
    return this._searchSubject
  }

  set extractor(fn: Extractor<T>) {
    this._extractor = fn
    this.fireSearch()
  }

  set dataSource(list: T[]) {
    this._ds = list
    this.fireSearch()
  }

  search(input: string): void {
    this._value = input
    this.fireSearch()
  }

  private fireSearch() {
    this._searchSubject.next(
      this._ds.filter(el => this._extractor(el).includes(this._value))
    )
  }
}
