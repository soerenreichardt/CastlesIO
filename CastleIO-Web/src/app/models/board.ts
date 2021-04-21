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
            x: Math.floor((boardPosition.x - this.offset.x + this.scale / 2) / this.scale),
            y: Math.floor((boardPosition.y - this.offset.y + this.scale / 2) / this.scale)
        };
    }

    isTaken(gamePosition: Point): boolean {
        return this.tiles.some(tile => {
            return tile.gameLocation.x === gamePosition.x && tile.gameLocation.y === gamePosition.y;
        });
    }

    addTile(tile: BoardTile): void {
        this.tiles.push(tile);
    }

}
