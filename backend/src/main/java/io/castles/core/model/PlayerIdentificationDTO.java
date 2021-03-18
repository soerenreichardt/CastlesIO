package io.castles.core.model;

import lombok.Value;

import java.util.UUID;

@Value
public class PlayerIdentificationDTO {
    UUID lobbyId;
    UUID playerId;
}
