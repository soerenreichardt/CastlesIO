import {Injectable, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {Observable} from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class LobbyService {
    baseUrl = environment.backendUrl + 'lobby/';
    lobbyUrl: string;
    lobbyEventSource: EventSource;

    constructor(private http: HttpClient) {
    }

    // returns playerId
    joinLobby(playerName: string): Observable<string> {
        return this.http.put<string>(this.lobbyUrl, {}, {
            params: {
                playerName
            }
        });
    }

    leaveLobby(playerId: string): Observable<void> {
        return this.http.delete<void>(this.lobbyUrl, {
            params: {
                playerId
            }
        });
    }

    // returns gameId
    startGame(): Observable<string> {
        return this.http.post<string>(this.lobbyUrl, {});
    }

    setLobbyBackendUrl(lobbyId: string): void {
        this.lobbyUrl = this.baseUrl + lobbyId + '/';
        this.lobbyEventSource = new EventSource(this.lobbyUrl + 'subscribe/');
    }
}
