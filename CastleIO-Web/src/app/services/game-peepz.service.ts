import {Injectable} from '@angular/core';
import {ReplaySubject} from 'rxjs';
import {PlayerDTO} from '../models/dtos/player-dto.interface';

@Injectable({
    providedIn: 'root'
})
export class GamePeepzService {

    players = new ReplaySubject<PlayerDTO[]>();

    constructor() {
    }
}
