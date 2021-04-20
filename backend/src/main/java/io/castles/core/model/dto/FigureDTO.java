package io.castles.core.model.dto;

import io.castles.core.tile.Figure;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FigureDTO {
    UUID ownerId;
    int x;
    int y;
    int row;
    int column;

    public static FigureDTO from(Figure figure) {
        return new FigureDTO(
                figure.getOwner().getId(),
                figure.getPosition().getX(),
                figure.getPosition().getY(),
                figure.getPosition().getRow(),
                figure.getPosition().getColumn()
        );
    }
}
