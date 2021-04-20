import {TileDTO} from './dtos/tile-dto';
import * as d3 from 'd3';
import {Point} from '@angular/cdk/drag-drop';

export class BoardTile {
    id: number;
    gameLocation: Point;
    rotation: number;
    tileDTO: TileDTO;
    image: HTMLImageElement;

    constructor(tileDTO: TileDTO, x: number, y: number, image: HTMLImageElement) {
        this.id = tileDTO.id;
        this.gameLocation = {x, y};
        this.rotation = tileDTO.tileLayout.rotation;
        this.tileDTO = tileDTO;
        this.image = image;
    }

    toTileDTO(): TileDTO {
        const originalTileDTO = this.tileDTO;
        originalTileDTO.tileLayout.rotation = this.rotation;
        return originalTileDTO;
    }
}
