import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {HttpClientModule} from '@angular/common/http';
import { LobbyComponent } from './lobby/lobby.component';
import { LandingComponent } from './landing/landing.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import { CreatePlayerComponent } from './lobby/create-player/create-player.component';
import { ModalDirective } from './directives/modal.directive';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {MatInputModule} from '@angular/material/input';
import {MatCheckboxModule} from '@angular/material/checkbox';
import {MatButtonModule} from '@angular/material/button';
import {MatListModule} from '@angular/material/list';
import {MatCardModule} from '@angular/material/card';
import {MatIconModule} from '@angular/material/icon';
import {MatDividerModule} from '@angular/material/divider';
import {MatSelectModule} from '@angular/material/select';
import {MatSliderModule} from '@angular/material/slider';
import {MatSnackBarModule} from '@angular/material/snack-bar';
import {ClipboardModule} from '@angular/cdk/clipboard';
import { GameComponent } from './game/game.component';
import { GameBoardComponent } from './game/game-board/game-board.component';
import { GamePeepzComponent } from './game/game-peepz/game-peepz.component';
import {MatDialogModule} from '@angular/material/dialog';

@NgModule({
    declarations: [
        AppComponent,
        LobbyComponent,
        LandingComponent,
        CreatePlayerComponent,
        ModalDirective,
        GameComponent,
        GameBoardComponent,
        GamePeepzComponent
    ],
    imports: [
        BrowserModule,
        HttpClientModule,
        AppRoutingModule,
        FormsModule,
        BrowserAnimationsModule,
        MatInputModule,
        MatCheckboxModule,
        MatButtonModule,
        MatListModule,
        MatCardModule,
        MatIconModule,
        MatDividerModule,
        MatSelectModule,
        MatSliderModule,
        MatSnackBarModule,
        MatDialogModule,
        ClipboardModule,
        ReactiveFormsModule
    ],
    providers: [],
    bootstrap: [AppComponent]
})
export class AppModule {
}
