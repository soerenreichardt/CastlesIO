import { Injectable } from '@angular/core';
import {ReplaySubject} from 'rxjs';
import {TileDTO} from '../models/dtos/tile-dto';
import {BoardTile} from '../models/boardTile';
import {GameService} from './game.service';
import {EventService} from './events/event.service';

@Injectable({
  providedIn: 'root'
})
export class GameBoardService {
    tiles = new ReplaySubject<Map<number, Map<number, TileDTO>>>();

    constructor(
        private gameService: GameService
    ) { }

    placeTile(playerId: string, tile: BoardTile): void {
        const tileDTO = tile.toTileDTO();
        const {x, y} = tile.getOriginalPosition();

        this.gameService.placeTile(playerId, tileDTO, x, y).subscribe();
    }


}
