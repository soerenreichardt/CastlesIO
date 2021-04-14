import {PlayerDTO} from './dtos/player-dto.interface';
import {GameStateDTO} from './dtos/game-state-dto.interface';
import {TileDTO} from './tile-dto';
import {GameDTO} from './dtos/game-dto.interface';
import {GameStates} from './game-states.enum';

export class Game {
    players: PlayerDTO[];
    gameState: GameStateDTO;
    tiles: Map<number, Map<number, TileDTO>>;

    myId: string;

    constructor(gameDTO: GameDTO, playerId: string) {
        this.players = gameDTO.players;
        this.gameState = gameDTO.gameState;
        this.tiles = gameDTO.tiles;
        this.myId = playerId;
    }

    timeToDrawTile(): boolean {
        const drawState = this.gameState.state === GameStates.Draw;
        return this.myTurn() && drawState;
    }

    timeToPlaceTile(): boolean {
        const placeState = this.gameState.state === GameStates.PlaceTile;
        return this.myTurn() && placeState;
    }

    myTurn(): boolean {
        return this.gameState.player.id === this.myId;
    }
}
