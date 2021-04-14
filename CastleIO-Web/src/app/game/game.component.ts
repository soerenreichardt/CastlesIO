import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {GameService} from '../services/game.service';
import {LocalStorageService} from '../services/local-storage.service';
import {DrawnTileService} from '../services/drawn-tile.service';
import {EventService} from '../services/events/event.service';
import {GameStates} from '../models/game-states.enum';
import {Game} from '../models/game';
import {placeholdersToParams} from '@angular/compiler/src/render3/view/i18n/util';

@Component({
    selector: 'app-game',
    templateUrl: './game.component.html',
    styleUrls: ['./game.component.scss']
})
export class GameComponent implements OnInit {
    gameId: string;
    playerId: string;

    game: Game;

    constructor(
        private activatedRoute: ActivatedRoute,
        private router: Router,
        private localStorageService: LocalStorageService,
        private gameService: GameService,
        private drawnTileService: DrawnTileService,
        private eventService: EventService
    ) {
    }

    ngOnInit(): void {
        this.gameId = this.activatedRoute.snapshot.params.id;
        this.gameService.setGameUrl(this.gameId);
        this.playerId = this.localStorageService.getObject(this.gameId).playerId;

        if (!this.playerId) {
            this.redirectUnauthenticatedPlayer();
        } else {
            this.eventService.subscribeToServerUpdates(this.gameId, this.playerId);
            this.gameService.getGame(this.playerId).subscribe(game => {
                this.game = game;

                if (game.timeToPlaceTile()) {
                    this.drawnTileService.getDrawnTile(this.playerId);
                }
            });
        }
    }

    drawTile(): void {
        this.drawnTileService.drawTile(this.playerId);
    }

    private redirectUnauthenticatedPlayer(): void {
        this.router.navigate(['']);
    }
}
