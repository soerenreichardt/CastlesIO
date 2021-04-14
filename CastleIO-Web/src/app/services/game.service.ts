import {Injectable} from '@angular/core';
import {environment} from '../../environments/environment';
import {HttpClient} from '@angular/common/http';
import {TileDTO} from '../models/tile-dto';
import {Observable} from 'rxjs';
import {GameDTO} from '../models/dtos/game-dto.interface';

@Injectable({
    providedIn: 'root'
})
export class GameService {
    baseUrl = environment.backendUrl + 'game/';
    gameUrl: string;

    constructor(private http: HttpClient) {
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

    getGame(playerId: string): Observable<GameDTO> {
        return this.http.get<GameDTO>(this.gameUrl, {
            params: {
                playerId
            }
        });
    }
}
