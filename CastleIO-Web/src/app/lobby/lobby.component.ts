import {ApplicationRef, Component, OnDestroy, OnInit} from '@angular/core';
import {LobbyService} from '../services/lobby.service';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
    selector: 'app-lobby',
    templateUrl: './lobby.component.html',
    styleUrls: ['./lobby.component.scss']
})
export class LobbyComponent implements OnInit, OnDestroy {
    playerName: string;
    lobbyId: string;
    playerId: string;
    sseDataLog: string[] = [];

    sseEventSource: EventSource;

    constructor(private lobbyService: LobbyService,
                private activatedRoute: ActivatedRoute,
                private router: Router,
                private appRef: ApplicationRef) {
    }

    ngOnInit(): void {
        this.lobbyId = this.activatedRoute.snapshot.params.id;
        this.lobbyService.setLobbyUrl(this.lobbyId);
    }

    ngOnDestroy(): void {
        this.sseEventSource.close();
    }

    joinLobby(): void {
        this.lobbyService.joinLobby(this.playerName).subscribe(playerId => {
            this.playerId = playerId;
            this.subscribeToLobbyChanges();
        }, error => console.log(error));
    }

    private subscribeToLobbyChanges(): void {
        this.sseEventSource = this.lobbyService.subscribeToLobbyUpdates(this.lobbyId, this.playerId);
        this.sseEventSource.onmessage = message => {
            this.sseDataLog.push(message.data);
            this.appRef.tick();
        };
    }
}
