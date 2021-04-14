import {PlayerDTO} from './player-dto.interface';
import {GameStates} from '../game-states.enum';

export interface GameStateDTO {
    state: GameStates;
    player: PlayerDTO;
}
