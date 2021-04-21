import {Injectable} from '@angular/core';
import {SvgService} from './svg.service';

@Injectable({
    providedIn: 'root'
})
export class FiguresService {
    NUMBER_OF_FIGURES = 7;

    figureImages: HTMLImageElement[];
    figureMask: HTMLImageElement;

    constructor(
        private svgService: SvgService
    ) {
        Promise.all(this.svgService.getFigures(this.NUMBER_OF_FIGURES)).then(figureImages => {
            this.figureImages = figureImages;
        });
        this.svgService.getFigureMask('#eeeeee').then(figureMask => {
            this.figureMask = figureMask;
        });
    }

    getFigures(): HTMLImageElement[] {
        return this.figureImages;
    }

    getRandomFigurePath(): string {
        const figureId = Math.ceil(Math.random() * this.NUMBER_OF_FIGURES);
        return `assets/figure/figure${figureId}.svg`;
    }
}
