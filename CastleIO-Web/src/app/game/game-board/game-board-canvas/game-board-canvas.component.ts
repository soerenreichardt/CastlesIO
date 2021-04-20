import {Component, OnInit} from '@angular/core';
import {GameBoardService} from '../../../services/game-board.service';
import {BoardTile} from '../../../models/boardTile';
import * as d3 from 'd3';
import {Board} from '../../../models/board';
import {Point} from '@angular/cdk/drag-drop';
import {DrawnTileService} from '../../../services/drawn-tile.service';
import {SvgService} from '../svg.service';
import {DrawnBoardTile} from '../../../models/drawnBoardTile';

@Component({
    selector: 'app-game-board-canvas',
    templateUrl: './game-board-canvas.component.html',
    styleUrls: ['./game-board-canvas.component.scss']
})
export class GameBoardCanvasComponent implements OnInit {
    board: Board;
    drawnTile: DrawnBoardTile;

    drawAreaBg: HTMLImageElement;

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
        this.initCanvas().then(() => {
            addEventListener('resize', () => this.updateOffset());

            this.gameBoardService.tiles.subscribe(tiles => {
                this.board = new Board(tiles);
                this.updateOffset();
            });

            this.drawnTileService.drawnTile.subscribe(drawnTile => {
                this.drawnTile = drawnTile;
                this.render();
            });
        });
    }

    private initCanvas(): Promise<void> {
        return new Promise(resolve => {
            this.canvas = d3.select<HTMLCanvasElement, any>('#game-board-canvas');
            this.canvasElement = this.canvas.node();
            this.context = this.canvasElement.getContext('2d');

            this.canvas.call(
                d3.drag()
                    .container( this.canvasElement)
                    .subject((event) => this.isDragTargetDrawnTile(event))
                    .on('start', (event) => this.dragStarted(event))
                    .on('drag', (event) => this.dragging(event))
                    .on('end', (event) => this.dragEnded(event))
            );

            this.svgService.getDrawAreaBackground().then(drawAreaBg => {
                this.drawAreaBg = drawAreaBg;
                resolve();
            });
        });
    }

    private isDragTargetDrawnTile(event): boolean {
        if (!this.drawnTile) {
            return false;
        }

        let drawnTilePosition = this.getDrawnTileInitialPosition();
        if (this.drawnTile.wasMovedToGameBoard) {
            drawnTilePosition = this.board.getBoardPosition(this.drawnTile);
        }

        const xOnBoardTile = drawnTilePosition.x <= event.x && drawnTilePosition.x + 100 >= event.x;
        const yOnBoardTile = drawnTilePosition.y <= event.y && drawnTilePosition.y + 100 >= event.y;

        return (xOnBoardTile && yOnBoardTile);
    }

    private dragStarted(event): void {
        console.log('drag started event:');
        console.log(event);
    }

    private dragging(event): void {
        if (event.subject) {
            const pointerPosition = {
                x: event.x,
                y: event.y
            };
            this.drawnTile.gameLocation = this.board.getGameFromBoardPosition(pointerPosition);
            this.drawnTile.wasMovedToGameBoard = true;
            this.render();
        }
    }

    private dragEnded(event): void {
        console.log('drag ended event:');
        console.log(event);
    }

    renderPlacedTiles(): void {
        this.board.tiles.forEach(tile => {
            this.renderTile(tile);
        });
    }

    private render(): void {
        this.context.clearRect(0, 0, this.canvasElement.offsetWidth, this.canvasElement.offsetHeight);
        this.renderDrawArea();

        if (this.board) {
            this.renderPlacedTiles();
        }

        if (this.drawnTile) {
            this.renderDrawnTile();
        }
    }

    private renderTile(boardTile: BoardTile): void {
        const boardPos = this.board.getBoardPosition(boardTile);
        this.context.drawImage(boardTile.image, boardPos.x, boardPos.y, 100, 100);
    }

    private renderDrawArea(): void {
        const drawAreaPosition: Point = {
            x: 0,
            y: this.canvasElement.offsetHeight - 320
        };
        this.context.drawImage(this.drawAreaBg, drawAreaPosition.x, drawAreaPosition.y, 400, 300);
        this.context.clearRect(25, this.canvasElement.offsetHeight - 245, 110, 110);
    }

    renderDrawnTile(): void {
        let drawTilePosition = this.getDrawnTileInitialPosition();
        let scale = 100;
        if (this.drawnTile.wasMovedToGameBoard) {
            drawTilePosition = this.board.getBoardPosition(this.drawnTile);
            scale = this.board.scale;
        }
        this.context.drawImage(
            this.drawnTile.image,
            drawTilePosition.x,
            drawTilePosition.y,
            scale,
            scale);
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

    private getDrawnTileInitialPosition(): Point {
        return {
            x: 30,
            y: this.canvasElement.offsetHeight - 240
        };
    }
}
