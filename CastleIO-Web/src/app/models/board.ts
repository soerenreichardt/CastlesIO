import {BoardTile} from './boardTile';
import {Point} from '@angular/cdk/drag-drop';
import {DrawnBoardTile} from './drawnBoardTile';

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

    doesTileHaveNeighbors(drawnTile: DrawnBoardTile): boolean {
        if (!drawnTile.wasMovedToGameBoard) {
            return false;
        }

        return this.tiles.some(tile => {
            if (tile.gameLocation.x === drawnTile.gameLocation.x) {
                return tile.gameLocation.y === drawnTile.gameLocation.y + 1 ||
                    tile.gameLocation.y === drawnTile.gameLocation.y - 1;
            }
            if (tile.gameLocation.y === drawnTile.gameLocation.y) {
                return tile.gameLocation.x === drawnTile.gameLocation.x + 1 ||
                    tile.gameLocation.x === drawnTile.gameLocation.x - 1;
            }
        });
    }

    addTile(tile: BoardTile): void {
        this.tiles.push(tile);
    }

}
