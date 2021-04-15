import {LobbySettings} from '../lobby-settings.interface';
import {PlayerDTO} from './player-dto.interface';

export interface LobbyDTO {
    players: PlayerDTO[];
    lobbyName: string;
    lobbySettings: LobbySettings;
}
