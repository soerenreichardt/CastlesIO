import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {ApiService} from '../services/api.service';

@Component({
    selector: 'app-landing',
    templateUrl: './landing.component.html',
    styleUrls: ['./landing.component.scss']
})
export class LandingComponent implements OnInit {
    lobbyName = '';

    constructor(private apiService: ApiService,
                private router: Router) {
    }

    ngOnInit(): void {
    }

    createLobby(): void {
        this.apiService.createLobby(this.lobbyName).subscribe(lobbyID => {
            this.router.navigate(['/lobby', lobbyID]);
        }, error => console.log(error));
    }
}
