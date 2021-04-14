import { Injectable } from '@angular/core';
import {ReplaySubject} from 'rxjs';
import {TileDTO} from '../models/tile-dto';

@Injectable({
  providedIn: 'root'
})
export class GameBoardService {
    tiles = new ReplaySubject<Map<number, Map<number, TileDTO>>>();

  constructor() { }
}
