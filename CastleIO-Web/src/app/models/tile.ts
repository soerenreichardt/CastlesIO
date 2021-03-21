import {TileDTO} from './tile-dto';

export class Tile {
    id: number;
    x: number;
    y: number;
    element: HTMLElement;
    rotation: number;

    constructor(tileDTO: TileDTO) {
        this.id = tileDTO.id;
        this.x = 0;
        this.y = 0;
        this.rotation = tileDTO.tileLayout.rotation;
        console.log(tileDTO);
    }
}
