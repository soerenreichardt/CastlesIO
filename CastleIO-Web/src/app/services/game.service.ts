import {Injectable} from '@angular/core';
import {environment} from '../../environments/environment';
import {HttpClient} from '@angular/common/http';
import {Tile} from '../models/tile';
import {TileDTO} from '../models/tile-dto';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';

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

    getNewTile(): Observable<Tile> {
        return this.http.get<TileDTO>(this.gameUrl + 'new_tile').pipe(
            map(tileDTO => Object.assign(new Tile(tileDTO)))
        );
    }
}
