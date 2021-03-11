import {Component, OnDestroy, OnInit} from '@angular/core';
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
                private router: Router) {
    }

    ngOnInit(): void {
        this.lobbyId = this.activatedRoute.snapshot.params.id;
        console.log('lobby id is ', this.lobbyId);
        this.sseEventSource = this.lobbyService.subscribeToLobbyUpdates(this.lobbyId);
        this.sseEventSource.onmessage = message => {
            this.sseDataLog.push(message.data);
        };
    }

    ngOnDestroy(): void {
        this.sseEventSource.close();
    }

    joinLobby(): void {
        this.lobbyService.joinLobby(this.playerName).subscribe(playerId => {
            this.playerId = playerId;
        }, error => console.log(error));
    }
}
