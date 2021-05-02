import {Injectable} from '@angular/core';
import {environment} from '../../../environments/environment';
import {ReplaySubject} from 'rxjs';
import {LobbySettings} from '../../models/lobby-settings.interface';
import {GameStartDTO} from '../../models/dtos/game-start-dto.interface';
import {PlayerDTO} from '../../models/dtos/player-dto.interface';
import {Router} from '@angular/router';
import {LobbyDTO} from '../../models/dtos/lobby-dto.interface';
import {PhaseSwitchDTO} from '../../models/dtos/phase-switch-dto.interface';
import {TileDTO} from '../../models/dtos/tile-dto';
import {GameStates} from '../../models/game-states.enum';
import {PlacedTileDTO} from '../../models/dtos/placed-tile-dto.interface';
import {TilePlacedDTO} from '../../models/dtos/tile-placed-dto.interface';

enum Events {
    // Server Events
    PlayerReconnected= 'PLAYER_RECONNECTED',
    PlayerDisconnected = 'PLAYER_DISCONNECTED',
    PlayerTimeout = 'PLAYER_TIMEOUT',
    // Lobby Events
    PlayerAdded = 'PLAYER_ADDED',
    PlayerRemoved = 'PLAYER_REMOVED',
    SettingsChanged = 'SETTINGS_CHANGED',
    LobbyCreated = 'LOBBY_CREATED',
    // Game Events
    GameStarted = 'GAME_STARTED',
    PhaseSwitched = 'PHASE_SWITCHED',
    ActivePlayerSwitched = 'ACTIVE_PLAYER_SWITCHED',
    TilePlaced = 'TILE_PLACED'
}

interface PlayerAdded {
    event: Events.PlayerAdded;
    payload: PlayerDTO;
}

interface PlayerReconnected {
    event: Events.PlayerReconnected;
    payload: PlayerDTO;
}

interface PlayerDisconnected {
    event: Events.PlayerDisconnected;
    payload: PlayerDTO;
}

interface PlayerTimeout {
    event: Events.PlayerTimeout;
    payload: PlayerDTO;
}

interface PlayerRemoved {
    event: Events.PlayerRemoved;
    payload: PlayerDTO;
}

interface SettingsChanged {
    event: Events.SettingsChanged;
    payload: LobbySettings;
}

interface LobbyCreated {
    event: Events.LobbyCreated;
    payload: LobbyDTO;
}

interface GameStarted {
    event: Events.GameStarted;
    payload: GameStartDTO;
}

interface PhaseSwitched {
    event: Events.PhaseSwitched;
    payload: PhaseSwitchDTO;
}

interface ActivePlayerSwitched {
    event: Events.ActivePlayerSwitched;
    payload: PlayerDTO;
}

interface TilePlaced {
    event: Events.TilePlaced;
    payload: TilePlacedDTO;
}

type EventType = PlayerAdded |
    PlayerReconnected |
    PlayerDisconnected |
    PlayerTimeout |
    PlayerRemoved |
    SettingsChanged |
    LobbyCreated |
    GameStarted |
    PhaseSwitched |
    ActivePlayerSwitched |
    TilePlaced;

@Injectable({
    providedIn: 'root'
})

export class EventService {
    playerAdded = new ReplaySubject<PlayerDTO>();
    playerReconnected = new ReplaySubject<PlayerDTO>();
    playerDisconnected = new ReplaySubject<PlayerDTO>();
    playerTimeout = new ReplaySubject<PlayerDTO>();
    playerRemoved = new ReplaySubject<PlayerDTO>();
    settingsChanged = new ReplaySubject<LobbySettings>();
    lobbyCreated = new ReplaySubject<LobbyDTO>();
    gameStarted = new ReplaySubject<GameStartDTO>();
    phaseSwitched = new ReplaySubject<GameStates>();
    activePlayerSwitched = new ReplaySubject<PlayerDTO>();
    tilePlaced = new ReplaySubject<TilePlacedDTO>();


    constructor(private router: Router) {
    }

    subscribeToServerUpdates(lobbyId: string, playerId: string): void {
        const websiteState = this.router.url.split('/')[1];
        const eventSource = new EventSource(`${environment.backendUrl}${websiteState}/${lobbyId}/subscribe/${playerId}`);
        console.log('subscribed to lobby updates');
        eventSource.onmessage = (message) => {
            const data: EventType = JSON.parse(message.data);
            console.log(data);
            if (data.event === Events.PlayerAdded) {
                this.playerAdded.next(data.payload);
            }

            if (data.event === Events.PlayerReconnected) {
                this.playerReconnected.next(data.payload);
            }

            if (data.event === Events.PlayerDisconnected) {
                this.playerDisconnected.next(data.payload);
            }

            if (data.event === Events.PlayerTimeout) {
                this.playerTimeout.next(data.payload);
            }

            if (data.event === Events.PlayerRemoved) {
                this.playerRemoved.next(data.payload);
            }

            if (data.event === Events.SettingsChanged) {
                this.settingsChanged.next(data.payload);
            }

            if (data.event === Events.LobbyCreated) {
                this.lobbyCreated.next(data.payload);
            }

            if (data.event === Events.GameStarted) {
                this.gameStarted.next(data.payload);
            }

            if (data.event === Events.PhaseSwitched) {
                this.phaseSwitched.next(data.payload.to);
            }

            if (data.event === Events.ActivePlayerSwitched) {
                this.activePlayerSwitched.next(data.payload);
            }

            if (data.event === Events.TilePlaced) {
                this.tilePlaced.next(data.payload);
            }
        };
    }
}
