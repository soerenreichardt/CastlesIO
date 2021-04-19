import {Injectable} from '@angular/core';
import {BoardTile} from '../../models/boardTile';
import * as d3 from 'd3';

@Injectable({
    providedIn: 'root'
})
export class SvgService {

    constructor() {
    }

    getStyledVectorDataBlob(boardTile: BoardTile): Promise<Blob> {
        return new Promise((resolve) => {
            d3.xml(`assets/tiles/tile1.svg`).then(xmlDom => {
                const svgElement = xmlDom.querySelector('svg');
                this.addPlayerStyles(svgElement);
                const svgBlob = new Blob([svgElement.outerHTML], {type: 'image/svg+xml;charset=utf-8'});
                resolve(svgBlob);
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
