import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {LandingComponent} from './landing/landing.component';
import {LobbyComponent} from './lobby/lobby.component';
import {GameComponent} from './game/game.component';

const routes: Routes = [
    {path: '', component: LandingComponent},
    {path: 'lobby/:id', component: LobbyComponent},
    {path: 'game/:id', component: GameComponent},
    {path: '*', redirectTo: ''}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
