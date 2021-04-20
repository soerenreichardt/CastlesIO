import {BoardTile} from './boardTile';
import {TileDTO} from './dtos/tile-dto';
import {Point} from '@angular/cdk/drag-drop';

export class DrawnBoardTile extends BoardTile{
    wasMovedToGameBoard: boolean;
    validPosition: boolean;

    constructor(tileDTO: TileDTO, image: HTMLImageElement) {
        super(tileDTO, 0, 0, image);
        this.wasMovedToGameBoard = false;
        this.validPosition = false;
    }

    toTileDTO(): TileDTO {
        if (!this.wasMovedToGameBoard) {
            throw new Error('tile has not yet been placed');
        }
        if (!this.validPosition) {
            throw new Error('tile position is invalid');
        }

        return super.toTileDTO();
    }
}
