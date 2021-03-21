import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GamePeepzComponent } from './game-peepz.component';

describe('GamePeepzComponent', () => {
  let component: GamePeepzComponent;
  let fixture: ComponentFixture<GamePeepzComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GamePeepzComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(GamePeepzComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
