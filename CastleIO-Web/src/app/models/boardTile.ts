import {TileDTO} from './tile-dto';
import * as d3 from 'd3';
import {Point} from '@angular/cdk/drag-drop';

export class BoardTile {
    id: number;
    x: number;
    y: number;
    rotation: number;
    tileDTO: TileDTO;

    constructor(tileDTO: TileDTO, x: number, y: number) {
        this.id = tileDTO.id;
        this.x = (x + 1) * 100;
        this.y = (y + 1) * 100;
        this.rotation = tileDTO.tileLayout.rotation;
        this.tileDTO = tileDTO;
    }

    getTileElement(): Promise<HTMLElement> {
        const tilePath = `assets/tiles/tile${this.id}.svg`;
        return d3.xml(tilePath).then(tileData => {
            const element = tileData.documentElement.querySelector<HTMLElement>(`g#tile${this.id}`);
            element.setAttribute('transform', `rotate(${this.rotation * 90})`)
            return element;
        });
    }

    getOriginalPosition(): Point {
        return {
            x: this.x / 100 - 1,
            y: this.y / 100 - 1
        };
    }

    toTileDTO(): TileDTO {
        const originalTileDTO = this.tileDTO;
        originalTileDTO.tileLayout.rotation = this.rotation;
        return originalTileDTO;
    }

}
