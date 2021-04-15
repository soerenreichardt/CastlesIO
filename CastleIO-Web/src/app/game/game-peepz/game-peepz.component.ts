import {Component, Input, OnInit} from '@angular/core';
import {PlayerDTO} from '../../models/dtos/player-dto.interface';
import {GameStates} from '../../models/game-states.enum';
import {EventService} from '../../services/events/event.service';

@Component({
    selector: 'app-game-peepz',
    templateUrl: './game-peepz.component.html',
    styleUrls: ['./game-peepz.component.scss']
})
export class GamePeepzComponent implements OnInit {
    @Input()
    players: PlayerDTO[];
    activePlayer: PlayerDTO;
    turnState: GameStates;

    activePlayerStateTexts = {
        DRAW: 'drawing',
        PLACE_TILE: 'placing a tile',
        PLACE_FIGURE: 'placing a figure'
    };

    constructor(
        private eventService: EventService
    ) {
    }

    ngOnInit(): void {
        this.eventService.activePlayerSwitched.subscribe(activePlayer => this.activePlayer = activePlayer);
        this.eventService.phaseSwitched.subscribe(gameState =>  this.turnState = gameState);
    }

}
