import {BoardTile} from './boardTile';
import {TileDTO} from './dtos/tile-dto';

export class DrawnBoardTile extends BoardTile{
    movedToGameBoard: boolean;
    validPosition: boolean;

    constructor(tileDTO: TileDTO) {
        super(tileDTO, 0, 0);
        this.movedToGameBoard = false;
        this.validPosition = false;
    }

    toTileDTO(): TileDTO {
        if (!this.movedToGameBoard) {
            throw new Error('tile has not yet been placed');
        }
        if (!this.validPosition) {
            throw new Error('tile position is invalid');
        }

        return super.toTileDTO();
    }
}
