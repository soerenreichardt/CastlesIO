import { Injectable } from '@angular/core';
import {PlayerAuthentication} from '../models/player-authentication.interface';

@Injectable({
  providedIn: 'root'
})
export class LocalStorageService {

  constructor() { }

  saveObjectForKey(object: object, key: string): void {
      localStorage.setItem(key, JSON.stringify(object));
  }

  getObject(key: string): any {
      const stringRepresentation = localStorage.getItem(key);
      return JSON.parse(stringRepresentation);
  }

  removeObject(key: string): void {
          localStorage.removeItem(key);
  }
}
