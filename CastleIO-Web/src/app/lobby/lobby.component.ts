import {Component, OnInit} from '@angular/core';
import {LobbyService} from '../services/lobby.service';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
    selector: 'app-lobby',
    templateUrl: './lobby.component.html',
    styleUrls: ['./lobby.component.scss']
})
export class LobbyComponent implements OnInit {
    playerName: string;
    lobbyId: string;
    playerId: string;

    constructor(private lobbyService: LobbyService,
                private activatedRoute: ActivatedRoute,
                private router: Router) {
    }

    ngOnInit(): void {
        this.lobbyId = this.activatedRoute.snapshot.params.id;
        this.lobbyService.setLobbyBackendUrl(this.lobbyId);
    }

    joinLobby(): void {
        this.lobbyService.joinLobby(this.playerName).subscribe(playerId => {
            this.playerId = playerId;
        }, error => console.log(error));
    }
}
