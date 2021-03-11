import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';

@Injectable({
    providedIn: 'root'
})
export class ApiService {
    backendUrl = environment.backendUrl;

    constructor(private http: HttpClient) {
    }

    createLobby(lobbyName: string): Observable<string> {
        return this.http.post<string>(this.backendUrl + 'lobby', {}, {
            params: {
                lobbyName
            }
        });
    }

}
