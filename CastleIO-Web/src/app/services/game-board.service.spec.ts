import { TestBed } from '@angular/core/testing';

import { GameBoardService } from './game-board.service';

describe('GameBoardService', () => {
  let service: GameBoardService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(GameBoardService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
