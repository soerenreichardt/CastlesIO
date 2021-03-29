package io.castles.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerIdentificationDTO {
    UUID lobbyId;
    UUID playerId;
}
