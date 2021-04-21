import {Component, Input, isDevMode, OnInit} from '@angular/core';
import {Game} from '../../../models/game';
import {DrawnTileService} from '../../../services/drawn-tile.service';
import {GameService} from '../../../services/game.service';

@Component({
    selector: 'app-game-board-controls',
    templateUrl: './game-board-controls.component.html',
    styleUrls: ['./game-board-controls.component.scss']
})
export class GameBoardControlsComponent implements OnInit {
    @Input()
    game: Game;
    isDevMode = isDevMode();

    constructor(
        private drawnTileService: DrawnTileService,
        private gameService: GameService
    ) {
    }

    ngOnInit(): void {
    }

    drawTile(): void {
        this.drawnTileService.drawTile(this.game.myId);
    }

    placeTile(): void {

    }

    placeFigure(): void {

    }

    skipPhase(): void {

    }

    debugDrawTile(): void {
        this.drawnTileService.debugDrawTile();
    }

    resetGame(): void {
        this.gameService.resetGame().subscribe();
    }
}
