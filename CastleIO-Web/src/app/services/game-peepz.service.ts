import {Injectable} from '@angular/core';
import {ReplaySubject} from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class GamePeepzService {

    tilesLeft = new ReplaySubject<number>();

    constructor() {
    }
}
