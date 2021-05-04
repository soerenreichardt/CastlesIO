package io.castles.core.model.dto;

import lombok.Value;

@Value
public class ScoreChangedDTO {
    PlayerDTO player;
    int newScore;
}
