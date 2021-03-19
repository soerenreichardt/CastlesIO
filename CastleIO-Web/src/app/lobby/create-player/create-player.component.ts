import {Component, OnInit, Output, EventEmitter, Input} from '@angular/core';
import {LobbyService} from '../../services/lobby.service';
import {PublicLobby} from '../../models/public-lobby.interface';

@Component({
    selector: 'app-create-player',
    templateUrl: './create-player.component.html',
    styleUrls: ['./create-player.component.scss']
})
export class CreatePlayerComponent implements OnInit {
    @Input() lobbyId: string;
    @Output() joinLobby = new EventEmitter<string>();

    lobbyInfo: PublicLobby;
    playerName: string;

    constructor(private lobbyService: LobbyService) {
    }

    ngOnInit(): void {
        this.lobbyService.getPublicLobbyInfo().subscribe(lobbyInfo => {
            this.lobbyInfo = lobbyInfo;
        });
    }

    join(): void  {
        this.joinLobby.emit(this.playerName);
    }
}
