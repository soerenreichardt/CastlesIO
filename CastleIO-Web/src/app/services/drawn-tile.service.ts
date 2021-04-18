import {Injectable} from '@angular/core';
import {ReplaySubject} from 'rxjs';
import {GameService} from './game.service';
import {TileDTO} from '../models/dtos/tile-dto';

@Injectable({
    providedIn: 'root'
})
export class DrawnTileService {
    drawnTile = new ReplaySubject<TileDTO>();

    constructor(
        private gameService: GameService
    ) {
    }

    drawTile(playerId: string): void {
        this.gameService.getNewTile(playerId).subscribe(tileDTO => {
            this.drawnTile.next(tileDTO);
        });
    }

    debugDrawTile(): void {
        this.drawnTile.next({
            id: 1,
            tileLayout: {
                rotation: 0,
                content: undefined
            }
        });
    }

    getDrawnTile(playerId: string): void {
        this.gameService.getDrawnTile(playerId).subscribe(tileDTO => {
            this.drawnTile.next(tileDTO);
        });
    }
}
