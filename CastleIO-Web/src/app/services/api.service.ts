import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {LobbySettings} from '../models/lobby-settings.interface';
import {UserAuthentication} from '../models/user-authentication.interface';
import {PublicLobby} from '../models/public-lobby.interface';

@Injectable({
    providedIn: 'root'
})
export class ApiService {
    backendUrl = environment.backendUrl;

    constructor(private http: HttpClient) {
    }

    getDefaultLobbySettings(): Observable<LobbySettings> {
        return this.http.get<LobbySettings>(this.backendUrl + 'settings');
    }

    createLobby(lobbyName: string, playerName: string, settings: LobbySettings): Observable<UserAuthentication> {
        return this.http.post<UserAuthentication>(this.backendUrl + 'lobby', settings, {
            params: {
                lobbyName,
                playerName
            }
        });
    }

    getPublicLobbies(): Observable<PublicLobby[]> {
        return this.http.get<PublicLobby[]>(this.backendUrl + 'lobbies');
    }
}
