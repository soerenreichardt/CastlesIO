import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {PlayerDTO} from '../../models/dtos/player-dto.interface';
import {GameStates} from '../../models/game-states.enum';
import {EventService} from '../../services/events/event.service';
import {GamePeepzService} from '../../services/game-peepz.service';

@Component({
    selector: 'app-game-peepz',
    templateUrl: './game-peepz.component.html',
    styleUrls: ['./game-peepz.component.scss']
})
export class GamePeepzComponent implements OnInit, OnDestroy {
    @Input()
    players: PlayerDTO[];

    activePlayer: PlayerDTO;
    turnState: GameStates;
    tilesLeft: number;

    activePlayerStateTexts = {
        DRAW: 'drawing',
        PLACE_TILE: 'placing a tile',
        PLACE_FIGURE: 'placing a figure'
    };

    constructor(
        private eventService: EventService,
        private gamePeepzService: GamePeepzService
    ) {
    }

    ngOnInit(): void {
        this.eventService.activePlayerSwitched.subscribe(activePlayer => this.activePlayer = activePlayer);
        this.eventService.phaseSwitched.subscribe(gameState =>  this.turnState = gameState);
        this.gamePeepzService.tilesLeft.subscribe( tilesLeft => this.tilesLeft = tilesLeft);
    }

    ngOnDestroy(): void {
        this.eventService.activePlayerSwitched.unsubscribe();
        this.eventService.phaseSwitched.unsubscribe();
    }
}
