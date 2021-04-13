import {Injectable} from '@angular/core';
import {environment} from '../../../environments/environment';
import {ReplaySubject} from 'rxjs';
import {LobbySettings} from '../../models/lobby-settings.interface';
import {GameStartDTO} from '../../models/dtos/game-start-dto.interface';
import {PlayerDTO} from '../../models/dtos/player-dto.interface';
import {ActivatedRoute, Router} from '@angular/router';

interface PlayerAdded {
    event: 'PLAYER_ADDED';
    payload: PlayerDTO;
}

interface PlayerReconnected {
    event: 'PLAYER_RECONNECTED';
    payload: PlayerDTO;
}

interface PlayerDisconnected {
    event: 'PLAYER_DISCONNECTED';
    payload: PlayerDTO;
}

interface PlayerTimeout {
    event: 'PLAYER_TIMEOUT';
    payload: PlayerDTO;
}

interface PlayerRemoved {
    event: 'PLAYER_REMOVED';
    payload: PlayerDTO;
}

interface GameStarted {
    event: 'GAME_STARTED';
    payload: GameStartDTO;
}

interface SettingsChanged {
    event: 'SETTINGS_CHANGED';
    payload: LobbySettings;
}

type EventType = PlayerAdded |
    PlayerReconnected |
    PlayerDisconnected |
    PlayerTimeout |
    PlayerRemoved |
    GameStarted |
    SettingsChanged;

@Injectable({
    providedIn: 'root'
})

export class EventService {
    playerAdded = new ReplaySubject<PlayerDTO>();
    playerReconnected = new ReplaySubject<PlayerDTO>();
    playerDisconnected = new ReplaySubject<PlayerDTO>();
    playerTimeout = new ReplaySubject<PlayerDTO>();
    playerRemoved = new ReplaySubject<PlayerDTO>();
    gameStarted = new ReplaySubject<GameStartDTO>();
    settingsChanged = new ReplaySubject<LobbySettings>();

    constructor(private router: Router) {
    }

    subscribeToServerUpdates(lobbyId: string, playerId: string): void {
        const websiteState = this.router.url.split('/')[1];
        const eventSource = new EventSource(`${environment.backendUrl}${websiteState}/${lobbyId}/subscribe/${playerId}`);
        console.log('subscribed to lobby updates');
        eventSource.onmessage = (message) => {
            const data: EventType = JSON.parse(message.data);
            console.log(data);
            if (data.event === 'PLAYER_ADDED') {
                this.playerAdded.next(data.payload);
            }

            if (data.event === 'PLAYER_RECONNECTED') {
                this.playerReconnected.next(data.payload);
            }

            if (data.event === 'PLAYER_DISCONNECTED') {
                this.playerDisconnected.next(data.payload);
            }

            if (data.event === 'PLAYER_TIMEOUT') {
                this.playerTimeout.next(data.payload);
            }

            if (data.event === 'PLAYER_REMOVED') {
                this.playerRemoved.next(data.payload);
            }

            if (data.event === 'GAME_STARTED') {
                this.gameStarted.next(data.payload);
            }

            if (data.event === 'SETTINGS_CHANGED') {
                data.payload.editable = true; // TODO: remove when authentication is implemented
                this.settingsChanged.next(data.payload);
            }
        };
    }
}
