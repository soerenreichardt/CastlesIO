import {Component, Input, isDevMode, OnInit} from '@angular/core';
import {Game} from '../../../models/game';
import {DrawnTileService} from '../../../services/drawn-tile.service';
import {GameService} from '../../../services/game.service';
import {GameBoardService} from '../../../services/game-board.service';
import {Board} from '../../../models/board';
import {DrawnBoardTile} from '../../../models/drawnBoardTile';

@Component({
    selector: 'app-game-board-controls',
    templateUrl: './game-board-controls.component.html',
    styleUrls: ['./game-board-controls.component.scss']
})
export class GameBoardControlsComponent implements OnInit {
    @Input()
    game: Game;
    board: Board;
    drawnTile: DrawnBoardTile;

    isDevMode = isDevMode();

    constructor(
        private drawnTileService: DrawnTileService,
        private gameBoardService: GameBoardService,
        private gameService: GameService
    ) {
    }

    ngOnInit(): void {
        this.gameBoardService.board.subscribe(board => {
            this.board = board;
        });
        this.drawnTileService.drawnTile.subscribe(drawnTile => {
            this.drawnTile = drawnTile;
        });
    }

    drawTile(): void {
        this.drawnTileService.drawTile(this.game.myId);
    }

    placeTile(): void {
        this.gameService.placeTile(
            this.game.myId,
            this.drawnTile.toTileDTO(),
            this.drawnTile.gameLocation.x,
            this.drawnTile.gameLocation.y
        ).subscribe(() => {
            this.drawnTileService.drawnTile.next(undefined);
        });
    }

    placeFigure(): void {

    }

    skipPhase(): void {
        this.gameService.skipPhase(this.game.myId).subscribe();
    }

    resetGame(): void {
        this.gameService.resetGame().subscribe();
    }

    drawnTilePositionValid(): boolean {
        if (this.drawnTile) {
            return this.drawnTile.validRotations.length > 0 && this.board.doesTileHaveNeighbors(this.drawnTile);
        }
        return false;
    }
}
