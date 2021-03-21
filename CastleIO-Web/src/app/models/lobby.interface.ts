import {LobbySettings} from './lobby-settings.interface';
import {PlayerDTO} from './dtos/player-dto.interface';

export interface Lobby {
    players: PlayerDTO[];
    lobbyName: string;
    lobbySettings: LobbySettings;
}
