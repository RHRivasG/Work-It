import { TestBed } from '@angular/core/testing';

import { TrainingsResolver } from './trainings.resolver';

describe('TrainingsResolver', () => {
  let resolver: TrainingsResolver;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    resolver = TestBed.inject(TrainingsResolver);
  });

  it('should be created', () => {
    expect(resolver).toBeTruthy();
  });
});
