import {ApplicationRef, Component, OnDestroy, OnInit} from '@angular/core';
import {LobbyService} from '../services/lobby.service';
import {ActivatedRoute, Router} from '@angular/router';
import {LocalStorageService} from '../services/local-storage.service';
import {PlayerInfo} from '../models/player-info.interface';
import {Lobby} from '../models/lobby.interface';
import {MatSnackBar} from '@angular/material/snack-bar';
import {Clipboard} from '@angular/cdk/clipboard';
import {debounce} from 'rxjs/operators';
import {interval} from 'rxjs';

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

    constructor(private lobbyService: LobbyService,
                private localStorageService: LocalStorageService,
                private activatedRoute: ActivatedRoute,
                private router: Router,
                private appRef: ApplicationRef,
                private snackBar: MatSnackBar,
                private clipboard: Clipboard) {
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

    updateLobbyStatus(lobby: Lobby): void {
        this.lobby = lobby;
        this.isLobbyPublic = lobby.lobbySettings.visibility === 'PUBLIC';
        console.log(this.lobby.lobbySettings);
    }

    initPostJoinProcess(): void {
        this.subscribeToLobbyChanges();
        this.savePlayerInfo();
        this.lobbyService.getLobbyStatus(this.playerId).subscribe(response => {
            this.updateLobbyStatus(response);
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
        this.lobbyService.updateLobbySettings(this.playerId, this.lobby.lobbySettings).subscribe();
    }


    copyUrlToClipboard(): void {
        this.clipboard.copy(this.inviteLink);
        this.showLinkCopiedSnackBar();
    }

    private subscribeToLobbyChanges(): void {
        this.sseEventSource = this.lobbyService.subscribeToLobbyUpdates(this.lobbyId, this.playerId);
        this.sseEventSource.onmessage = (message) => {
            this.updateLobbyStatus(JSON.parse(message.data));
        };
    }

    private savePlayerInfo(): void {
        this.localStorageService.saveObjectForKey({
            playerId: this.playerId,
            playerName: this.playerName
        }, this.lobbyId);
    }

    private setPlayerInfoFromStorage(): void {
        const savedPlayerData: PlayerInfo = this.localStorageService.getObject(this.lobbyId);
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
}
