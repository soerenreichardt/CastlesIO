import { TestBed } from '@angular/core/testing';

import { GamePeepzService } from './game-peepz.service';

describe('GamePeepzService', () => {
  let service: GamePeepzService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(GamePeepzService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
