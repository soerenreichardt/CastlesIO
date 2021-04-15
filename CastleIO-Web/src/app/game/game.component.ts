import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {GameService} from '../services/game.service';
import {LocalStorageService} from '../services/local-storage.service';
import {DrawnTileService} from '../services/drawn-tile.service';
import {EventService} from '../services/events/event.service';
import {Game} from '../models/game';
import {GameBoardService} from '../services/game-board.service';
import {GameBoardComponent} from './game-board/game-board.component';

@Component({
    selector: 'app-game',
    templateUrl: './game.component.html',
    styleUrls: ['./game.component.scss']
})
export class GameComponent implements OnInit, OnDestroy {
    @ViewChild(GameBoardComponent) gameBoardComponent: GameBoardComponent;

    gameId: string;
    playerId: string;

    game: Game;

    constructor(
        private activatedRoute: ActivatedRoute,
        private router: Router,
        private localStorageService: LocalStorageService,
        private gameService: GameService,
        private gameBoardService: GameBoardService,
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
                this.gameBoardService.tiles.next(game.tiles);
                this.keepGameUpToDate();
                if (game.timeToPlaceTile()) {
                    this.drawnTileService.getDrawnTile(this.playerId);
                }
            });
        }
    }

    keepGameUpToDate(): void {
        this.eventService.phaseSwitched.subscribe(phase => {
            this.game.gameState.state = phase;
        });
    }

    drawTile(): void {
        this.drawnTileService.drawTile(this.playerId);
    }

    placeTile(): void {
        this.gameBoardComponent.placeTile();
    }

    private redirectUnauthenticatedPlayer(): void {
        this.router.navigate(['']);
    }

    ngOnDestroy(): void {
        this.eventService.phaseSwitched.unsubscribe();
    }
}
