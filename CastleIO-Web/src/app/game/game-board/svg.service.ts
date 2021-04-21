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

    getFigures(amount: number): Promise<HTMLImageElement>[] {
        const imagePromises = [];
        Array(amount).fill(0).forEach((value, index) => {
            const imagePromise = new Promise(resolve => {
                const figurePath = `assets/figures/figure${index + 1}.svg`;
                const figureImage = new Image();
                figureImage.src = figurePath;
                figureImage.onload = (() => {
                    resolve(figureImage);
                });
            });

            imagePromises.push(imagePromise);
        });

        return imagePromises;
    }

    getFigureMask(color: string): Promise<HTMLImageElement> {
        return new Promise(resolve => {
            d3.xml(`assets/figures/figure_mask.svg`).then(xmlDom => {
                const svgElement = xmlDom.querySelector('svg');
                this.changeFigureColor(svgElement, color);
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

    private changeFigureColor(figure: SVGSVGElement, color: string): void {
        const paths = figure.querySelectorAll('path');
        paths.forEach(path => {
            this.changeFillColor(path, color);
        });
    }

    getDrawAreaBackground(): Promise<HTMLImageElement> {
        return new Promise<HTMLImageElement>(resolve => {
            const drawAreaBg = new Image();
            drawAreaBg.src = 'assets/images/player-material_bg.svg';
            drawAreaBg.onload = (() => {
                resolve(drawAreaBg);
            });
        });
    }

    private addPlayerStyles(svgElement: Element): void {
        return; // TODO: implement based on backend implementation
        const grass = svgElement.querySelector('#grass1');
        this.changeFillColor(grass, '#dbadcc');
    }

    private changeFillColor(element: Element, color: string): void {
        const styles = element.getAttribute('style');
        const playerColor = 'fill:#2578ff';
        const playerColoredStyles = styles.replace(/fill:#[a-zA-Z0-9]{6,}/, `fill:${color}`);
        element.setAttribute('style', playerColoredStyles);
    }
}
