import {ApplicationRef, Component, OnDestroy, OnInit} from '@angular/core';
import {LobbyService} from '../services/lobby.service';
import {ActivatedRoute, Router} from '@angular/router';
import {LocalStorageService} from '../services/local-storage.service';
import {PlayerAuthentication} from '../models/player-authentication.interface';
import {Lobby} from '../models/lobby.interface';
import {MatSnackBar} from '@angular/material/snack-bar';
import {Clipboard} from '@angular/cdk/clipboard';
import {interval} from 'rxjs';
import {HttpErrorResponse} from '@angular/common/http';
import {MatDialog} from '@angular/material/dialog';
import {EventService} from '../services/events/event.service';
import {PlayerDTO} from '../models/dtos/player-dto.interface';

@Component({
    selector: 'app-lobby',
    templateUrl: './lobby.component.html',
    styleUrls: ['./lobby.component.scss']
})
export class LobbyComponent implements OnInit, OnDestroy {
    lobby: Lobby;
    isLobbyPublic: boolean;
    lobbyId: string;
    playerId: string;
    playerName: string;
    inviteLink: string;

    sseEventSource: EventSource;

    updateLobbySettingsDebounceTimer: number;

    constructor(private lobbyService: LobbyService,
                private localStorageService: LocalStorageService,
                private activatedRoute: ActivatedRoute,
                private router: Router,
                private appRef: ApplicationRef,
                private snackBar: MatSnackBar,
                private matDialog: MatDialog,
                private clipboard: Clipboard,
                private eventService: EventService) {
    }

    ngOnInit(): void {
        this.inviteLink = window.location.href;
        this.lobbyId = this.activatedRoute.snapshot.params.id;
        this.lobbyService.setLobbyUrl(this.lobbyId);
        this.setPlayerInfoFromStorage();
        interval(1000).subscribe( () => {
            this.appRef.tick();
        });
    }

    ngOnDestroy(): void {
        if (this.sseEventSource) {
            this.sseEventSource.close();
        }
    }

    joinLobby(playerName: string): void {
        this.lobbyService.joinLobby(playerName).subscribe(playerId => {
            this.playerId = playerId;
            this.playerName = playerName;

            this.initPostJoinProcess();
        }, error => console.log(error));
    }

    initPostJoinProcess(): void {
        this.savePlayerInfo();
        this.lobbyService.getLobbyStatus(this.playerId).subscribe(lobby => {
            console.log(lobby);
            this.lobby = lobby;
            this.isLobbyPublic = lobby.lobbySettings.visibility === 'PUBLIC';
            this.subscribeToLobbyChanges();
        }, (error: HttpErrorResponse) => {
            if (error.status === 500) {
                this.snackBar.open('The lobby does not exist.', '', {duration: 3000});
                setTimeout(() => {
                    this.router.navigate(['']);
                }, 3000);
            }
        });
    }

    changeLobbyVisibility(): void {
        if (this.isLobbyPublic) {
            this.lobby.lobbySettings.visibility = 'PUBLIC';
        } else {
            this.lobby.lobbySettings.visibility = 'PRIVATE';
        }

        this.updateLobbySettings();
    }

    updateLobbySettings(): void {
        clearTimeout(this.updateLobbySettingsDebounceTimer);
        this.updateLobbySettingsDebounceTimer = setTimeout(() => {
            this.lobbyService.updateLobbySettings(this.playerId, this.lobby.lobbySettings).subscribe();
        }, 500);
    }

    startGame(): void {
        this.lobbyService.startGame().subscribe((gameId: string) => {
            this.navigateToGame(gameId);
        });
    }

    private navigateToGame(gameId: string): void {
        this.router.navigate(['game', gameId]);
    }

    copyUrlToClipboard(): void {
        this.clipboard.copy(this.inviteLink);
        this.showLinkCopiedSnackBar();
    }

    private subscribeToLobbyChanges(): void {
        this.eventService.subscribeToServerUpdates(this.lobbyId, this.playerId);

        this.eventService.settingsChanged.subscribe( lobbySettings => this.lobby.lobbySettings = lobbySettings);
        this.eventService.playerAdded.subscribe( player => this.addPlayer(player));
        this.eventService.playerRemoved.subscribe(this.removePlayer);
        this.eventService.gameStarted.subscribe(gameStartDTO => this.navigateToGame(gameStartDTO.gameId));
    }

    private savePlayerInfo(): void {
        this.localStorageService.saveObjectForKey({
            playerId: this.playerId,
            playerName: this.playerName
        }, this.lobbyId);
    }

    private setPlayerInfoFromStorage(): void {
        const savedPlayerData: PlayerAuthentication = this.localStorageService.getObject(this.lobbyId);
        if (savedPlayerData) {
            this.playerId = savedPlayerData.playerId;
            this.playerName = savedPlayerData.playerName;

            this.initPostJoinProcess();
        }
    }

    private showLinkCopiedSnackBar(): void {
        this.snackBar.open('Link copied to clipboard', '', {
            duration: 5000
        });
    }

    private addPlayer(playerToAdd: PlayerDTO): void {
        this.lobby.players.forEach(player => {
            if (player.id === playerToAdd.id) {
                return;
            }
        });

        this.lobby.players.push(playerToAdd);
    }

    private removePlayer(playerToRemove: PlayerDTO): void {
        this.lobby.players.filter(player => {
            return player !== playerToRemove;
        });
    }
}
