import {Injectable, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {Observable} from 'rxjs';
import {PublicLobby} from '../models/public-lobby.interface';
import {LobbyDTO} from '../models/dtos/lobby-dto.interface';
import {LobbySettings} from '../models/lobby-settings.interface';
import {GameStartDTO} from '../models/dtos/game-start-dto.interface';

@Injectable({
    providedIn: 'root'
})
export class LobbyService {
    baseUrl = environment.backendUrl + 'lobby/';
    lobbyUrl: string;

    constructor(private http: HttpClient) {
    }

    setLobbyUrl(lobbyId: string): void {
        this.lobbyUrl = this.baseUrl + lobbyId + '/';
    }

    joinLobby(playerName: string): Observable<string> {
        return this.http.put<string>(this.lobbyUrl + 'join/', {}, {
            params: {
                playerName
            }
        });
    }

    getPublicLobbyInfo(): Observable<PublicLobby> {
        return this.http.get<PublicLobby>(this.lobbyUrl + 'info/');
    }

    getLobbyStatus(playerId: string): Observable<LobbyDTO> {
        return this.http.get<LobbyDTO>(this.lobbyUrl + 'status/' + playerId);
    }

    updateLobbySettings(playerId: string, settings: LobbySettings): Observable<any> {
        return this.http.post(this.lobbyUrl + 'update/', settings, {
            params: {
                playerId
            }
        });
    }

    leaveLobby(playerId: string): Observable<void> {
        return this.http.delete<void>(this.lobbyUrl + 'leave/', {
            params: {
                playerId
            }
        });
    }

    startGame(): Observable<GameStartDTO> {
        return this.http.post<GameStartDTO>(this.lobbyUrl + 'start/', {});
    }

}
