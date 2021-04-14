import {Injectable} from '@angular/core';
import {ReplaySubject} from 'rxjs';
import {GameService} from './game.service';
import {Tile} from '../models/tile';
import * as d3 from 'd3';
import {TileDTO} from '../models/tile-dto';

@Injectable({
    providedIn: 'root'
})
export class DrawnTileService {
    drawnTile = new ReplaySubject<Tile>();

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
        const tile = new Tile(tileDTO);
        const tilePath = `assets/tiles/tile${tile.id}.svg`;
        d3.xml(tilePath).then(tileData => {
            tile.element = tileData.documentElement.querySelector<HTMLElement>(`g#tile${tile.id}`);
            this.drawnTile.next(tile);
        });
    }

}
