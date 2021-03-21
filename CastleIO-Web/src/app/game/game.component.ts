import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {GameService} from '../services/game.service';

@Component({
    selector: 'app-game',
    templateUrl: './game.component.html',
    styleUrls: ['./game.component.scss']
})
export class GameComponent implements OnInit {
    gameId: string;
    playerId: string;

    constructor(
        private activatedRoute: ActivatedRoute,
        private gameService: GameService
    ) {
    }

    ngOnInit(): void {
        this.gameId = this.activatedRoute.snapshot.params.id;
        this.gameService.setGameUrl(this.gameId);
    }

}
