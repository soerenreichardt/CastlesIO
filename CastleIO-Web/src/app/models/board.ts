import {BoardTile} from './boardTile';
import {Point} from '@angular/cdk/drag-drop';

export class Board {
    offset: Point;
    scale: number;
    tiles: BoardTile[];

    constructor(tiles: BoardTile[]) {
        this.offset = {x: 0, y: 0};
        this.scale = 100;
        this.tiles = tiles;
    }

    getBoardPosition(boardTile: BoardTile): Point {
        return {
            x: (boardTile.gameLocation.x * this.scale) + this.offset.x,
            y: (boardTile.gameLocation.y * this.scale) + this.offset.y
        };
    }

    getGameFromBoardPosition(boardPosition: Point): Point {
        return {
            x: Math.floor((boardPosition.x - this.offset.x) / this.scale),
            y: Math.floor((boardPosition.y - this.offset.y) / this.scale)
        };
    }

    addTile(tile: BoardTile): void {
        this.tiles.push(tile);
    }

}
