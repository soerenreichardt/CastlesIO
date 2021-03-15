import {LobbySettings} from './lobby-settings.interface';

export interface Lobby {
    playerNames: string[];
    lobbyName: string;
    lobbySettings: LobbySettings;
}
