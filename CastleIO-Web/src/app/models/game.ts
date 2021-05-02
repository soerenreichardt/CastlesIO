import {PlayerDTO} from './dtos/player-dto.interface';
import {GameStateDTO} from './dtos/game-state-dto.interface';
import {TileDTO} from './dtos/tile-dto';
import {GameDTO} from './dtos/game-dto.interface';
import {GameStates} from './game-states.enum';

export class Game {
    name: string;
    players: PlayerDTO[];
    gameState: GameStateDTO;
    tiles: Map<number, Map<number, TileDTO>>;
    playerFiguresLeft: Map<string, number>;
    tilesLeft: number;

    myId: string;

    constructor(gameDTO: GameDTO, playerId: string) {
        this.name = gameDTO.name;
        this.players = gameDTO.players;
        this.gameState = gameDTO.gameState;
        this.tiles = gameDTO.tiles;
        this.playerFiguresLeft = gameDTO.playerFiguresLeft;
        this.myId = playerId;
        this.tilesLeft = gameDTO.tilesLeft;
    }

    timeToDrawTile(): boolean {
        const drawState = this.gameState.state === GameStates.Draw;
        return this.myTurn() && drawState;
    }

    timeToPlaceTile(): boolean {
        const placeTileState = this.gameState.state === GameStates.PlaceTile;
        return this.myTurn() && placeTileState;
    }

    timeToPlaceFigure(): boolean {
        const placeFigureState = this.gameState.state === GameStates.PlaceFigure;
        return this.myTurn() && placeFigureState;
    }

    myTurn(): boolean {
        return this.gameState.player.id === this.myId;
    }

    getOwnFiguresLeft(): number {
        return this.playerFiguresLeft[this.myId];
    }
}
