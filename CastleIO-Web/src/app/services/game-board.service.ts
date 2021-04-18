import { Injectable } from '@angular/core';
import {ReplaySubject} from 'rxjs';
import {TileDTO} from '../models/dtos/tile-dto';
import {BoardTile} from '../models/boardTile';
import {GameService} from './game.service';

@Injectable({
  providedIn: 'root'
})
export class GameBoardService {
    tiles = new ReplaySubject<BoardTile[]>();

    constructor(
        private gameService: GameService
    ) { }

    placeTile(playerId: string, tile: BoardTile): void {
        const tileDTO = tile.toTileDTO();
        const {x, y} = tile.gameLocation;

        this.gameService.placeTile(playerId, tileDTO, x, y).subscribe();
    }

    addTilesFromMap(mapTiles: Map<number, Map<number, TileDTO>>): void {
        const boardTiles = [];
        Object.keys(mapTiles).forEach(x => {
            const xVal = mapTiles[x];
            Object.keys(xVal).forEach(y => {
                const tileDTO = xVal[y];
                const boardTile = new BoardTile(tileDTO, Number(x), Number(y));
                boardTiles.push(boardTile);
            });
        });
        this.tiles.next(boardTiles);
    }
}
