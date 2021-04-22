import {Injectable} from '@angular/core';
import {BehaviorSubject, Observable, ReplaySubject, Subject} from 'rxjs';
import {TileDTO} from '../models/dtos/tile-dto';
import {BoardTile} from '../models/boardTile';
import {GameService} from './game.service';
import {SvgService} from '../game/game-board/svg.service';
import {Board} from '../models/board';
import {PlacedTileDTO} from '../models/dtos/placed-tile-dto.interface';

@Injectable({
  providedIn: 'root'
})
export class GameBoardService {
    board = new BehaviorSubject<Board>(undefined);
    figuresLeft = new ReplaySubject<number>();

    renderBoard = new Subject<void>();

    constructor(
        private gameService: GameService,
        private svgService: SvgService
    ) { }

    getMatchingTileRotations(tile: BoardTile): Observable<number[]> {
        const tileDTO = tile.toTileDTO();
        const {x, y} = tile.gameLocation;
        tileDTO.tileLayout.rotation = 0;

        return this.gameService.getMatchingTileRotations(tileDTO, x, -y);
    }

    placeTile(playerId: string, tile: BoardTile): void {
        const tileDTO = tile.toTileDTO();
        const {x, y} = tile.gameLocation;

        this.gameService.placeTile(playerId, tileDTO, x, y).subscribe();
    }

    addPlacedTile(placedTile: PlacedTileDTO): void {
        this.svgService.getTileImage(placedTile.tile).then(tileImage => {
            const boardTile = new BoardTile(placedTile.tile, placedTile.x, placedTile.y, tileImage);
            const board = this.board.getValue();
            board.addTile(boardTile);
            this.renderBoard.next();
        });

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
            const board = new Board(boardTiles);
            this.board.next(board);
            const elapsedTime = Date.now() - startTime;
            console.log(`Converted tiles map and loaded tile images in ${elapsedTime}ms.`);
        });
    }
}
