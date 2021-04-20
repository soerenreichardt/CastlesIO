import {PlayerAuthentication} from '../player-authentication.interface';
import {LobbySettings} from '../lobby-settings.interface';
import {TileDTO} from './tile-dto';

export interface GameStartDTO {
    gameId: string;
    players: PlayerAuthentication[];
    startingPlayer: PlayerAuthentication;
    gameSettings: LobbySettings;
    tileDTO: TileDTO;
}
