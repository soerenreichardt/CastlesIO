import {GameStateDTO} from './game-state-dto.interface';
import {TileDTO} from '../tile-dto';
import {PlayerDTO} from './player-dto.interface';

export interface GameDTO {
    gameState: GameStateDTO;
    tiles: Map<number, Map<number, TileDTO>>;
    players: PlayerDTO[];
}
