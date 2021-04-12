import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {ApiService} from '../services/api.service';
import {PublicLobby} from '../models/public-lobby.interface';
import {LobbySettings} from '../models/lobby-settings.interface';
import {LocalStorageService} from '../services/local-storage.service';
import {PlayerAuthentication} from '../models/player-authentication.interface';

@Component({
    selector: 'app-landing',
    templateUrl: './landing.component.html',
    styleUrls: ['./landing.component.scss']
})
export class LandingComponent implements OnInit {

    newLobbyName = '';
    newPlayerName = '';
    newLobbySettings: LobbySettings;

    isLobbyPublic: boolean;
    publicLobbies: PublicLobby[];

    constructor(private apiService: ApiService,
                private localStorageService: LocalStorageService,
                private router: Router) {
    }

    ngOnInit(): void {
        this.apiService.getDefaultLobbySettings().subscribe(lobbySettings => {
            this.newLobbySettings = lobbySettings;
            this.isLobbyPublic = lobbySettings.visibility === 'PUBLIC';
        }, error => {
            // TODO: display error modal
        });
        this.apiService.getPublicLobbies().subscribe( response => this.publicLobbies = response);

    }

    changeLobbyVisibility(): void {
        if (this.isLobbyPublic) {
            this.newLobbySettings.visibility = 'PUBLIC';
        } else {
            this.newLobbySettings.visibility = 'PRIVATE';
        }
    }

    createLobby(): void {
        this.apiService.createLobby(this.newLobbyName, this.newPlayerName, this.newLobbySettings).subscribe(userAuthentication => {
            const lobbyId = userAuthentication.lobbyId;
            const playerInfo = {
                playerName: this.newPlayerName,
                playerId: userAuthentication.playerId
            };
            this.localStorageService.saveObjectForKey(playerInfo, lobbyId);
            this.joinLobby(lobbyId);
        }, error => console.log(error));
    }

    joinLobby(lobbyId: string): void {
        this.router.navigate(['/lobby', lobbyId]);
    }
}
