import {Injectable} from '@angular/core';
import {ReplaySubject} from 'rxjs';
import {GameService} from './game.service';
import {TileDTO} from '../models/dtos/tile-dto';
import {DrawnBoardTile} from '../models/drawnBoardTile';

@Injectable({
    providedIn: 'root'
})
export class DrawnTileService {
    drawnTile = new ReplaySubject<DrawnBoardTile>();

    constructor(
        private gameService: GameService
    ) {
    }

    drawTile(playerId: string): void {
        this.gameService.getNewTile(playerId).subscribe(tileDTO => {
            const drawnTile = new DrawnBoardTile(tileDTO);
            this.drawnTile.next(drawnTile);
        });
    }

    debugDrawTile(): void {
        const tileId = Math.ceil(Math.random() * 19);
        const debugTileDTO = {
            id: tileId,
            tileLayout: {
                rotation: 0,
                content: undefined
            }
        };

        const drawnTile = new DrawnBoardTile(debugTileDTO);
        this.drawnTile.next(drawnTile);
    }

    getDrawnTile(playerId: string): void {
        this.gameService.getDrawnTile(playerId).subscribe(tileDTO => {
            const drawnTile = new DrawnBoardTile(tileDTO);
            this.drawnTile.next(drawnTile);
        });
    }
}
