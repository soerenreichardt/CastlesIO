import {TileDTO} from './tile-dto';
import * as d3 from 'd3';

export class Tile {
    id: number;
    x: number;
    y: number;
    rotation: number;

    constructor(tileDTO: TileDTO) {
        this.id = tileDTO.id;
        this.x = 0;
        this.y = 0;
        this.rotation = tileDTO.tileLayout.rotation;
    }

    getTileElement(): Promise<HTMLElement> {
        const tilePath = `assets/tiles/tile${this.id}.svg`;
        return d3.xml(tilePath).then(tileData => {
            return tileData.documentElement.querySelector<HTMLElement>(`g#tile${this.id}`);
        });
    }
}
