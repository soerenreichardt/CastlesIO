import {Injectable} from '@angular/core';
import {ReplaySubject} from 'rxjs';
import {GameService} from './game.service';
import {TileDTO} from '../models/dtos/tile-dto';
import {DrawnBoardTile} from '../models/drawnBoardTile';
import {SvgService} from '../game/game-board/svg.service';

@Injectable({
    providedIn: 'root'
})
export class DrawnTileService {
    drawnTile = new ReplaySubject<DrawnBoardTile>();

    constructor(
        private gameService: GameService,
        private svgService: SvgService
    ) {
    }

    drawTile(playerId: string): void {
        this.gameService.getNewTile(playerId).subscribe(tileDTO => {
            this.setDrawnTileFromDTO(tileDTO);
        });
    }

    getDrawnTile(playerId: string): void {
        this.gameService.getDrawnTile(playerId).subscribe(tileDTO => {
            this.setDrawnTileFromDTO(tileDTO);
        });
    }

    private setDrawnTileFromDTO(tileDTO: TileDTO): void {
        this.svgService.getTileImage(tileDTO).then(tileImage => {
            const drawnTile = new DrawnBoardTile(tileDTO, tileImage);
            this.drawnTile.next(drawnTile);
        });
    }
}
