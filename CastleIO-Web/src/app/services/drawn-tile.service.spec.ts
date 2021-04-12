import { TestBed } from '@angular/core/testing';

import { DrawnTileService } from './drawn-tile.service';

describe('DrawnTileService', () => {
  let service: DrawnTileService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DrawnTileService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
