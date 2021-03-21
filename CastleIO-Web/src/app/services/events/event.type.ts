import {PlayerAuthentication} from '../../models/player-authentication.interface';
import {LobbySettings} from '../../models/lobby-settings.interface';

export interface ServerEvent {
    event: string;
    payload: unknown;
}
