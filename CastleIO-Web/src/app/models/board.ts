import {BoardTile} from './boardTile';
import {TileDTO} from './tile-dto';

export class Board {
    tiles: BoardTile[] = [];
    boardWidth: number;
    boardHeight: number;

    constructor(tileMap: Map<number, Map<number, TileDTO>>) {
        Object.values(tileMap).forEach((value, x) => {
            Object.values<TileDTO>(value).forEach((tileDTO, y) => {
                this.tiles.push(new BoardTile(tileDTO, x, y));
            });
        });
        this.updateBoardDimensions();
    }

    addTile(tile: BoardTile): void {
        this.tiles.push(tile);
        this.updateBoardDimensions();
    }

    private updateBoardDimensions(): void {
        let smallestX = 0;
        let biggestX = 1;
        let smallestY = 0;
        let biggestY = 1;
        this.tiles.forEach(tile => {
            if (tile.x < smallestX) {
                smallestX = tile.x;
            }
            if (tile.y < smallestY) {
                smallestY = tile.y;
            }
            if (tile.x + 1 > biggestX) {
                biggestX = tile.x + 1;
            }
            if (tile.y + 1 > biggestY) {
                biggestY = tile.y + 1;
            }
        });

        this.boardWidth = biggestX - smallestX;
        this.boardHeight = biggestY - smallestY;
    }
}