package io.castles.core.model.dto;

import io.castles.core.tile.Meeple;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MeepleDTO {
    UUID ownerId;
    int x;
    int y;
    int row;
    int column;

    public static MeepleDTO from(Meeple meeple) {
        return new MeepleDTO(
                meeple.getOwner().getId(),
                meeple.getPosition().getX(),
                meeple.getPosition().getY(),
                meeple.getPosition().getRow(),
                meeple.getPosition().getColumn()
        );
    }
}
