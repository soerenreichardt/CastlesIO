import {Injectable} from '@angular/core';
import {environment} from '../../environments/environment';
import {HttpClient} from '@angular/common/http';
import {TileDTO} from '../models/tile-dto';
import {Observable} from 'rxjs';
import {GameDTO} from '../models/dtos/game-dto.interface';
import {Game} from '../models/game';
import {map} from 'rxjs/operators';
import {EventService} from './events/event.service';

@Injectable({
    providedIn: 'root'
})
export class GameService {
    baseUrl = environment.backendUrl + 'game/';
    gameUrl: string;


    constructor(
        private http: HttpClient,
        private eventService: EventService
    ) {
    }

    setGameUrl(gameId: string): void {
        this.gameUrl = this.baseUrl + gameId + '/';
    }

    getNewTile(playerId: string): Observable<TileDTO> {
        return this.http.get<TileDTO>(this.gameUrl + 'new_tile', {
            params: {
                playerId
            }
        });
    }

    getGame(playerId: string): Observable<Game> {
        return this.http.get<GameDTO>(this.gameUrl, {
            params: {
                playerId
            }
        }).pipe(map(gameDTO => {
            const game = new Game(gameDTO, playerId);
            this.initiateGameReplaySubjects(game);
            return game;
        }));
    }

    initiateGameReplaySubjects(game: Game): void {
        this.eventService.activePlayerSwitched.next(game.gameState.player);
        this.eventService.phaseSwitched.next(game.gameState.state);
    }

    getDrawnTile(playerId: string): Observable<TileDTO> {
        return this.http.get<TileDTO>(this.gameUrl + 'drawn_tile', {
            params: {
                playerId
            }
        });
    }
}
