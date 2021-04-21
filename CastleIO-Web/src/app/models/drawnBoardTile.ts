import {BoardTile} from './boardTile';
import {TileDTO} from './dtos/tile-dto';
import {Point} from '@angular/cdk/drag-drop';
import {Subject} from 'rxjs';

export class DrawnBoardTile extends BoardTile{
    wasMovedToGameBoard: boolean;
    animatingRotation: number;
    dragging = false;
    validRotations: number[];

    validRotationsChanged = new Subject<void>();

    constructor(tileDTO: TileDTO, image: HTMLImageElement) {
        super(tileDTO, 0, 0, image);
        this.wasMovedToGameBoard = false;
        this.validRotations = [0, 1, 2, 3];
    }

    setValidRotation(validRotations: number[]): void {
        this.validRotations = validRotations;
        this.validRotationsChanged.next();
    }

    toTileDTO(): TileDTO {
        if (!this.wasMovedToGameBoard) {
            throw new Error('tile has not yet been placed');
        }

        return super.toTileDTO();
    }
}
