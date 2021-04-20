import {Injectable} from '@angular/core';
import * as d3 from 'd3';
import {TileDTO} from '../../models/dtos/tile-dto';

@Injectable({
    providedIn: 'root'
})
export class SvgService {

    constructor() {
    }

    getTileImage(tile: TileDTO): Promise<HTMLImageElement> {
        return new Promise((resolve) => {
            d3.xml(`assets/tiles/curvy/tile${tile.id}.svg`).then(xmlDom => {
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

    getDrawAreaBackground(): Promise<HTMLImageElement> {
        return new Promise<HTMLImageElement>((resolve) => {
            const drawAreaBg = new Image();
            drawAreaBg.src = 'assets/images/player-material_bg.svg';
            drawAreaBg.onload = (() => {
                resolve(drawAreaBg);
            });
        });
    }

    private addPlayerStyles(svgElement: Element): void {
        return; // TODO: implement based on backend implementation
        const svgStyles = svgElement.querySelector('#grass1').getAttribute('style');
        const playerColor = 'fill:#2578ff';
        const playerColoredStyles = svgStyles.replace(/fill:#[a-zA-Z0-9]{6,}/, playerColor);
        svgElement.querySelector('#grass1').setAttribute('style', playerColoredStyles);
    }
}
