package io.castles.core.model.dto;

import io.castles.game.GameSettings;
import io.castles.game.Player;
import lombok.Value;

import java.util.List;
import java.util.UUID;

@Value
public class GameStartDTO {
    UUID gameId;
    List<Player> players;
    Player startingPlayer;
    GameSettings settings;
    TileDTO startTile;
}
