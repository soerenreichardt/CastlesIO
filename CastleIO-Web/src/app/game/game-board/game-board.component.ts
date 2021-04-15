import {Component, OnInit} from '@angular/core';
import * as d3 from 'd3';
import {BoardTile} from '../../models/boardTile';
import {Board} from '../../models/board';
import {DrawnTileService} from '../../services/drawn-tile.service';
import {TileDTO} from '../../models/tile-dto';
import {GameBoardService} from '../../services/game-board.service';

@Component({
    selector: 'app-game-board',
    templateUrl: './game-board.component.html',
    styleUrls: ['./game-board.component.scss']
})
export class GameBoardComponent implements OnInit {
    scale = 100;
    drawnTile: BoardTile;
    board: Board;
    canvas;

    constructor(
        private drawnTileService: DrawnTileService,
        private gameBoardService: GameBoardService
    ) {
    }

    ngOnInit(): void {

        this.drawnTileService.drawnTile.subscribe(drawnTile => {
            this.drawnTile = drawnTile;
            this.renderGameBoard();
        });
        this.gameBoardService.tiles.subscribe(tiles => {
            this.board = new Board(tiles);
            this.initCanvas();
            this.renderGameBoard();
        });
    }

    placeTile(playerId: string): void {
        this.gameBoardService.placeTile(playerId, this.drawnTile);
    }

    private initCanvas(): void {
        this.canvas = d3.select('#canvas-container')
            .append('svg')
            .attr('id', 'canvas')
            .attr('width', this.board.boardWidth + 200)
            .attr('height', this.board.boardHeight + 200)
            .attr('class', 'canvas')
            .style('border', '2px solid black');
    }

    private renderGameBoard(): void {
        this.renderPlacedTiles();
        if (this.drawnTile) {
            this.renderDrawnTile(this.drawnTile);
        }
    }

    private renderPlacedTiles(): void {
        console.log(this.board.tiles);
        this.board.tiles.forEach(tile => {
            tile.getTileElement().then(element => {
                d3.select('.canvas')
                    .append('svg')
                    .datum(tile)
                    .attr('viewBox', '0 0 64 64')
                    .attr('xmlns', 'http://www.w3.org/2000/svg')
                    .attr('width', this.scale)
                    .attr('height', this.scale)
                    .attr('x', (d) => d.x)
                    .attr('y', (d) => d.y)
                    .node().appendChild(element);
            });
        });
    }

    private renderDrawnTile(tile: BoardTile): void {
        const dragHandler = d3.drag()
            .on('drag', function(event, d: BoardTile): void {
                const scale = 100;
                d3.select(this)
                    .attr('x',  d.x = Math.floor( (event.x + 50) / scale) * scale)
                    .attr('y',  d.y = Math.floor( (event.y + 50) / scale) * scale);
            });

        function clicked(event: Event, d: BoardTile): void {
            if (event.defaultPrevented) { return; }
            d.rotation = (d.rotation + 1) % 4;
            d3.select(this.firstChild).transition().attr('transform', `rotate(${d.rotation * 90})`);
        }

        tile.getTileElement().then(element => {
            d3.select('.canvas')
                .append('svg')
                .datum(tile)
                .attr('viewBox', '0 0 64 64')
                .attr('xmlns', 'http://www.w3.org/2000/svg')
                .attr('width', this.scale)
                .attr('height', this.scale)
                .attr('x', (d) => d.x * this.scale)
                .attr('y', (d) => d.y * this.scale)
                .call(dragHandler)
                .on('click', clicked)
                .node().appendChild(element);
        });
    }
}
