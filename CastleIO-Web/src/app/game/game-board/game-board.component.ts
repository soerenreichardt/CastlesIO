import {Component, OnInit} from '@angular/core';
import * as d3 from 'd3';
import {Tile} from '../../models/tile';
import {PlacedTiles} from '../../models/placed-tiles';
import {DrawnTileService} from '../../services/drawn-tile.service';

@Component({
    selector: 'app-game-board',
    templateUrl: './game-board.component.html',
    styleUrls: ['./game-board.component.scss']
})
export class GameBoardComponent implements OnInit {
    boardWidth = 3;
    boardHeight = 3;
    scale = 100;

    drawnTile: Tile;
    placesTiles: PlacedTiles;
    canvas;

    constructor(
        private drawnTileService: DrawnTileService,
    ) {
    }

    ngOnInit(): void {
        this.initCanvas();

        this.drawnTileService.drawnTile.subscribe(drawnTile => {
            if (drawnTile !== undefined) {
                this.drawnTile = drawnTile;
                this.renderGameBoard();
            }
        });
    }

    private initCanvas(): void {
        this.canvas = d3.select('#canvas-container')
            .append('svg')
            .attr('id', 'canvas')
            .attr('width', this.boardWidth * this.scale)
            .attr('height', this.boardHeight * this.scale)
            .attr('class', 'canvas')
            .style('border', '2px solid black');
    }

    private renderGameBoard(): void {
        const dragHandler = d3.drag()
            .on('drag', function(event, d: Tile): void {
                const scale = 100;
                d3.select(this)
                    .attr('x',  d.x = Math.floor( (event.x + 50) / scale) * scale)
                    .attr('y',  d.y = Math.floor( (event.y + 50) / scale) * scale);
            });

        function clicked(event: Event, d: Tile): void {
            if (event.defaultPrevented) { return; }
            d.rotation = (d.rotation + 1) % 4;
            d3.select(this.firstChild).transition().attr('transform', `rotate(${d.rotation * 90})`);
        }

        d3.select('.canvas')
            .append('svg')
            .datum(this.drawnTile)
            .attr('viewBox', '0 0 64 64')
            .attr('xmlns', 'http://www.w3.org/2000/svg')
            .attr('width', this.scale)
            .attr('height', this.scale)
            .attr('x', (d) => d.x * this.scale)
            .attr('y', (d) => d.y * this.scale)
            .call(dragHandler)
            .on('click', clicked)
            .node().appendChild(this.drawnTile.element);
    }
}
