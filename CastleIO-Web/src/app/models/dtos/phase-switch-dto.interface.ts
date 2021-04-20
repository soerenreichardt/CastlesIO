import {GameStates} from '../game-states.enum';

export interface PhaseSwitchDTO {
    from: GameStates;
    to: GameStates;
}
