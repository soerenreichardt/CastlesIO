import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {GameService} from '../services/game.service';
import {LocalStorageService} from '../services/local-storage.service';
import {DrawnTileService} from '../services/drawn-tile.service';

@Component({
    selector: 'app-game',
    templateUrl: './game.component.html',
    styleUrls: ['./game.component.scss']
})
export class GameComponent implements OnInit {
    gameId: string;
    playerId: string;

    constructor(
        private activatedRoute: ActivatedRoute,
        private localStorageService: LocalStorageService,
        private gameService: GameService,
        private drawnTileService: DrawnTileService
    ) {
    }

    ngOnInit(): void {
        this.gameId = this.activatedRoute.snapshot.params.id;
        this.gameService.setGameUrl(this.gameId);
        this.playerId = this.localStorageService.getObject(this.gameId).playerId;
    }

    drawTile(): void {
        this.drawnTileService.drawTile(this.playerId);
    }
}
