import {GameStateDTO} from './game-state-dto.interface';
import {TileDTO} from './tile-dto';
import {PlayerDTO} from './player-dto.interface';

export interface GameDTO {
    name: string;
    gameState: GameStateDTO;
    tiles: Map<number, Map<number, TileDTO>>;
    players: PlayerDTO[];
    playerFiguresLeft: Map<string, number>;
    tilesLeft: number;
}
