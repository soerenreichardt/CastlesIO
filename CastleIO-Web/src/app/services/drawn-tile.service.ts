import {Injectable} from '@angular/core';
import {ReplaySubject} from 'rxjs';
import {GameService} from './game.service';
import {BoardTile} from '../models/boardTile';
import * as d3 from 'd3';
import {TileDTO} from '../models/dtos/tile-dto';

@Injectable({
    providedIn: 'root'
})
export class DrawnTileService {
    drawnTile = new ReplaySubject<BoardTile>();

    constructor(
        private gameService: GameService
    ) {
    }

    drawTile(playerId: string): void {
        this.gameService.getNewTile(playerId).subscribe(tileDTO => {
            this.setDrawnTile(tileDTO);
        });
    }

    getDrawnTile(playerId: string): void {
        this.gameService.getDrawnTile(playerId).subscribe(tileDTO => {
            this.setDrawnTile(tileDTO);
        });
    }

    private setDrawnTile(tileDTO: TileDTO): void {
        this.drawnTile.next(new BoardTile(tileDTO, -1, -1));
    }

}
