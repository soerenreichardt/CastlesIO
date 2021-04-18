import {TileDTO} from './dtos/tile-dto';
import * as d3 from 'd3';
import {Point} from '@angular/cdk/drag-drop';

export class BoardTile {
    id: number;
    gameLocation: Point;
    rotation: number;
    tileDTO: TileDTO;

    constructor(tileDTO: TileDTO, x: number, y: number) {
        this.id = tileDTO.id;
        this.gameLocation = {x, y};
        this.rotation = tileDTO.tileLayout.rotation;
        this.tileDTO = tileDTO;
    }

    getTileElement(): Promise<any> {
        const tilePath = `assets/tiles/tile${this.id}.svg`;
        return d3.xml(tilePath).then(tileData => {
            const element = tileData.documentElement.querySelector<HTMLElement>(`g#tile${this.id}`);
            element.setAttribute('transform', `rotate(${this.rotation * 90})`);
            console.log(tileData.documentElement);
            return tileData.documentElement;
        });
    }

    toTileDTO(): TileDTO {
        const originalTileDTO = this.tileDTO;
        originalTileDTO.tileLayout.rotation = this.rotation;
        return originalTileDTO;
    }
}
