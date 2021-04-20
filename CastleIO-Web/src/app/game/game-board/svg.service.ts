import {Injectable} from '@angular/core';
import {BoardTile} from '../../models/boardTile';
import * as d3 from 'd3';
import {TileDTO} from '../../models/dtos/tile-dto';
import {GameBoardService} from '../../services/game-board.service';
import {TileGraphics} from '../../models/tile-graphics.type';

@Injectable({
    providedIn: 'root'
})
export class SvgService {

    tileGraphics: TileGraphics;

    constructor(
        private gameBoardService: GameBoardService
    ) {
        this.gameBoardService.tileGraphics.subscribe(tileGraphics => this.tileGraphics = tileGraphics);
    }

    getTileImage(tile: BoardTile | TileDTO): Promise<HTMLImageElement> {
        return new Promise((resolve) => {
            d3.xml(`assets/tiles/${this.tileGraphics}/tile${tile.id}.svg`).then(xmlDom => {
                const svgElement = xmlDom.querySelector('svg');
                this.addPlayerStyles(svgElement);
                const svgBlob = new Blob([svgElement.outerHTML], {type: 'image/svg+xml;charset=utf-8'});
                const url = URL.createObjectURL(svgBlob);

                const tileImage = new Image();
                tileImage.src = url;

                tileImage.onload = (() => {
                    resolve(tileImage);
                });
            });
        });
    }

    addPlayerStyles(svgElement: Element): void {
        return; // TODO: implement based on backend implementation
        const svgStyles = svgElement.querySelector('#grass1').getAttribute('style');
        const playerColor = 'fill:#2578ff';
        const playerColoredStyles = svgStyles.replace(/fill:#[a-zA-Z0-9]{6,}/, playerColor);
        svgElement.querySelector('#grass1').setAttribute('style', playerColoredStyles);
    }
}
