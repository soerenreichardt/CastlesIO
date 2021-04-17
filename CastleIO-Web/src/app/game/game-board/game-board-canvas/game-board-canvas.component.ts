import {Component, OnInit} from '@angular/core';
import {TileDTO} from '../../../models/dtos/tile-dto';

@Component({
    selector: 'app-game-board-canvas',
    templateUrl: './game-board-canvas.component.html',
    styleUrls: ['./game-board-canvas.component.scss']
})
export class GameBoardCanvasComponent implements OnInit {
    tileScale = 100;
    drawnTile: TileDTO;

    canvas: HTMLElement;
    visibleArea: HTMLElement;
    context: CanvasRenderingContext2D;

    constructor() {
    }

    ngOnInit(): void {
        this.canvas = document.getElementById('canvas-area');
        this.visibleArea = document.getElementById('visible-area');

        console.log(`canvas dimensions area (${this.canvas.offsetWidth}x${this.canvas.offsetHeight}`);
        console.log(`visible area of the canvas is (${this.visibleArea.offsetWidth}x${this.visibleArea.offsetHeight})`);
    }


}
