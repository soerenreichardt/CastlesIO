import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GameBoardCanvasComponent } from './game-board-canvas.component';

describe('GameBoardCanvasComponent', () => {
  let component: GameBoardCanvasComponent;
  let fixture: ComponentFixture<GameBoardCanvasComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GameBoardCanvasComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(GameBoardCanvasComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
