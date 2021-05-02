import {ApplicationRef, Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {GameService} from '../services/game.service';
import {LocalStorageService} from '../services/local-storage.service';
import {DrawnTileService} from '../services/drawn-tile.service';
import {EventService} from '../services/events/event.service';
import {Game} from '../models/game';
import {GameBoardService} from '../services/game-board.service';
import {MatSnackBar} from '@angular/material/snack-bar';
import {HttpErrorResponse} from '@angular/common/http';

@Component({
    selector: 'app-game',
    templateUrl: './game.component.html',
    styleUrls: ['./game.component.scss']
})
export class GameComponent implements OnInit, OnDestroy {
    gameId: string;
    playerId: string;

    game: Game;

    constructor(
        private localStorageService: LocalStorageService,
        private gameService: GameService,
        private gameBoardService: GameBoardService,
        private drawnTileService: DrawnTileService,
        private eventService: EventService,

        private activatedRoute: ActivatedRoute,
        private router: Router,
        private snackBar: MatSnackBar,
        private applicationRef: ApplicationRef
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
                this.gameBoardService.addTilesFromMap(game.tiles);
                this.keepGameUpToDate();
                if (game.timeToPlaceTile()) {
                    this.drawnTileService.getDrawnTile(this.playerId);
                }
            }, (error: HttpErrorResponse) => {
                if (error.status === 500) {
                    this.redirectIfGameDoesNotExist();
                }
            });
        }
    }

    keepGameUpToDate(): void {
        this.eventService.phaseSwitched.subscribe(phase => {
            this.game.gameState.state = phase;
            this.applicationRef.tick();
        });
        this.eventService.activePlayerSwitched.subscribe(player => {
           this.game.gameState.player = player;
           this.applicationRef.tick();
        });
        this.eventService.tilePlaced.subscribe(placedTile => {
            this.gameBoardService.addPlacedTile(placedTile);
        });
        this.gameBoardService.figuresLeft.next(this.game.getOwnFiguresLeft());
    }

    private redirectUnauthenticatedPlayer(): void {
        this.snackBar.open('You are not part of this game.', '', {duration: 3000});
        setTimeout(() => {
            this.router.navigate(['']);
        }, 3000);
    }

    private redirectIfGameDoesNotExist(): void {
        this.snackBar.open('This game does not exist.', '', {duration: 3000});
        setTimeout(() => {
            this.router.navigate(['']);
        }, 3000);
    }

    ngOnDestroy(): void {
        this.eventService.phaseSwitched.unsubscribe();
        this.eventService.activePlayerSwitched.unsubscribe();
    }
}
