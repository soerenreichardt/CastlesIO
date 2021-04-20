import {Injectable} from '@angular/core';
import {BehaviorSubject, ReplaySubject} from 'rxjs';
import {TileDTO} from '../models/dtos/tile-dto';
import {BoardTile} from '../models/boardTile';
import {GameService} from './game.service';
import {TileGraphics} from '../models/tile-graphics.type';
import {SvgService} from '../game/game-board/svg.service';

@Injectable({
  providedIn: 'root'
})
export class GameBoardService {
    tiles = new ReplaySubject<BoardTile[]>();
    tileGraphics = new BehaviorSubject<TileGraphics>('curvy');

    constructor(
        private gameService: GameService,
        private svgService: SvgService
    ) { }

    placeTile(playerId: string, tile: BoardTile): void {
        const tileDTO = tile.toTileDTO();
        const {x, y} = tile.gameLocation;

        this.gameService.placeTile(playerId, tileDTO, x, y).subscribe();
    }

    addTilesFromMap(mapTiles: Map<number, Map<number, TileDTO>>): void {
        const startTime = Date.now();

        const boardTiles = [];
        const imageLoadPromises = [];
        Object.keys(mapTiles).forEach(x => {
            const xVal = mapTiles[x];
            Object.keys(xVal).forEach(y => {
                const tileDTO = xVal[y];
                imageLoadPromises.push(this.svgService.getTileImage(tileDTO).then( tileImage => {
                    const boardTile = new BoardTile(tileDTO, Number(x), Number(y), tileImage);
                    boardTiles.push(boardTile);
                }));
            });
        });
        Promise.all(imageLoadPromises).then(() => {
            this.tiles.next(boardTiles);
            const elapsedTime = Date.now() - startTime;
            console.log(`Converted tiles map and loaded tile images in ${elapsedTime}ms.`);
        });
    }
}
