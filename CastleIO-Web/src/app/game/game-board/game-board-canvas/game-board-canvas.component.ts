import {Component, OnInit} from '@angular/core';
import {TileDTO} from '../../../models/dtos/tile-dto';
import {GameBoardService} from '../../../services/game-board.service';
import {BoardTile} from '../../../models/boardTile';
import * as d3 from 'd3';
import {Board} from '../../../models/board';
import {Point} from '@angular/cdk/drag-drop';
import {DrawnTileService} from '../../../services/drawn-tile.service';
import {SvgService} from '../svg.service';

@Component({
    selector: 'app-game-board-canvas',
    templateUrl: './game-board-canvas.component.html',
    styleUrls: ['./game-board-canvas.component.scss']
})
export class GameBoardCanvasComponent implements OnInit {
    board: Board;
    drawnTile: BoardTile;

    canvas: d3.Selection<HTMLCanvasElement, any, HTMLElement, any>;
    canvasElement: HTMLCanvasElement;
    context: CanvasRenderingContext2D;

    constructor(
        private gameBoardService: GameBoardService,
        private drawnTileService: DrawnTileService,
        private svgService: SvgService
    ) {
    }

    ngOnInit(): void {
        this.canvas = d3.select<HTMLCanvasElement, any>('#game-board-canvas');
        this.canvasElement = this.canvas.node();
        this.context = this.canvasElement.getContext('2d');

        addEventListener('resize', () => this.updateOffset());

        this.gameBoardService.tiles.subscribe(tiles => {
            this.board = new Board(tiles);
            this.updateOffset();
        });

        this.drawnTileService.drawnTile.subscribe(drawnTile => {
            this.setDrawnTile(drawnTile);
        });

        this.canvas
            .call( d3.drag()
                .container( this.canvasElement)
                .subject((event) => this.getHoveredTile(event))
            );
    }

    private getHoveredTile(event): BoardTile {
        const mouse = d3.pointer(event);
        console.log(mouse);
        return this.board.tiles[0];
    }

    renderPlacedTiles(): void {
        this.board.tiles.forEach(tile => {
            this.renderTile(tile);
        });
    }

    setDrawnTile(drawnTile: TileDTO): void {
        this.drawnTile = new BoardTile(
            drawnTile,
            30,
            this.canvasElement.offsetHeight - 240
        );
        this.render();
    }



    private render(): void {
        if (this.board) {
            this.renderPlacedTiles();
        }

        this.renderDrawArea().then(() => {
            if (this.drawnTile) {
                this.renderDrawnTile();
            }
        });
    }

    private renderTile(boardTile: BoardTile): void {
        this.svgService.getStyledVectorDataBlob(boardTile).then((tileBlob) => {
            const url = URL.createObjectURL(tileBlob);

            const tileImage = new Image();
            tileImage.src = url;

            tileImage.onload = (event => {
                const boardPos = this.board.getBoardPosition(boardTile);
                this.context.drawImage(tileImage, boardPos.x, boardPos.y, 100, 100);
            });
        });
    }

    private renderDrawArea(): Promise<void> {
        return new Promise<void>((resolve) => {
            const drawAreaPosition: Point = {
                x: 0,
                y: this.canvasElement.offsetHeight - 320
            };
            const drawAreaBg = new Image();
            drawAreaBg.src = 'assets/images/player-material_bg.svg';
            drawAreaBg.onload = (() => {
                this.context.drawImage(drawAreaBg, drawAreaPosition.x, drawAreaPosition.y, 400, 300);
                this.context.clearRect(25, this.canvasElement.offsetHeight - 245, 110, 110);
                resolve();
            });

        });
    }

    renderDrawnTile(): void {
        const drawnTileImage = new Image();
        drawnTileImage.src = `assets/tiles/tile1.svg`;

        drawnTileImage.onload = (event => {
            this.context.drawImage(
                drawnTileImage,
                this.drawnTile.gameLocation.x,
                this.drawnTile.gameLocation.y,
                100,
                100);
        });
    }

    private updateOffset(): void {
        this.board.offset = {
            x: this.canvasElement.offsetWidth / 2,
            y: this.canvasElement.offsetHeight / 2
        };
        this.canvasElement.width = this.canvasElement.offsetWidth;
        this.canvasElement.height = this.canvasElement.offsetHeight;
        this.render();
    }
}
