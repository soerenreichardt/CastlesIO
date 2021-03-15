import {Injectable, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {Observable} from 'rxjs';
import {PublicLobby} from '../models/public-lobby.interface';
import {Lobby} from '../models/lobby.interface';

@Injectable({
    providedIn: 'root'
})
export class LobbyService {
    baseUrl = environment.backendUrl + 'lobby/';
    lobbyUrl: string;

    constructor(private http: HttpClient) {
    }

    // returns playerId
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

    getLobbyStatus(playerId: string): Observable<Lobby> {
        return this.http.get<Lobby>(this.lobbyUrl + 'status/' + playerId);
    }

    leaveLobby(playerId: string): Observable<void> {
        return this.http.delete<void>(this.lobbyUrl + 'leave/', {
            params: {
                playerId
            }
        });
    }

    // returns gameId
    startGame(): Observable<string> {
        return this.http.post<string>(this.lobbyUrl + 'start/', {});
    }

    subscribeToLobbyUpdates(lobbyId: string, playerId: string): EventSource {
        return new EventSource(this.lobbyUrl + 'subscribe/' + playerId);
    }

    setLobbyUrl(lobbyId: string): void {
        this.lobbyUrl = this.baseUrl + lobbyId + '/';
    }
}
