import { TestBed } from '@angular/core/testing';

import { TrainingResolver } from './training.resolver';

describe('TrainingResolver', () => {
  let resolver: TrainingResolver;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    resolver = TestBed.inject(TrainingResolver);
  });

  it('should be created', () => {
    expect(resolver).toBeTruthy();
  });
});
