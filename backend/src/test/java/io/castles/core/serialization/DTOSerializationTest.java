package io.castles.core.serialization;

import io.castles.core.GameMode;
import io.castles.core.model.dto.*;
import io.castles.core.tile.MatrixTileLayout;
import io.castles.core.tile.Tile;
import io.castles.core.tile.TileContent;
import io.castles.core.util.JsonHelper;
import io.castles.game.*;
import io.castles.game.events.EventHandler;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class DTOSerializationTest {


    static PlayerDTO player = PlayerDTO.from(new Player("P1"));
    static GameLobbySettings lobbySettings = GameLobbySettings.builder().build();
    static GameSettings gameSettings = GameSettings.from(GameLobbySettings.builder().gameMode(GameMode.DEBUG).build());
    static Tile tile = Tile.drawStatic(TileContent.GRAS);
    static GameLobby gameLobby = new GameLobby("Test", player.toPlayer(), new EventHandler());

    private static Stream<Arguments> serializableDTOs() {
        return Stream.concat(
                Stream.of(
                    Arguments.of("GameStartDTO", GameStartDTO.class, new GameStartDTO(UUID.randomUUID(), List.of(player), player, GameSettingsDTO.from(gameSettings), TileDTO.from(tile))),
                    Arguments.of("GameStateDTO", GameStateDTO.class, new GameStateDTO(GameState.START, player)),
                    Arguments.of("LobbyStateDTO", LobbyStateDTO.class, LobbyStateDTO.from(gameLobby)),
                    Arguments.of("PlayerDTO", PlayerDTO.class, player),
                    Arguments.of("PublicLobbyDTO", PublicLobbyDTO.class, PublicLobbyDTO.from(gameLobby)),
                    Arguments.of("TileContentMatrixDTO", TileContentMatrixDTO.class, TileContentMatrixDTO.from(tile.<MatrixTileLayout>getTileLayout().getContent())),
                    Arguments.of("TileDTO", TileDTO.class, TileDTO.from(tile)),
                    Arguments.of("TileLayoutDTO", TileLayoutDTO.class, TileLayoutDTO.from(tile.getTileLayout())),
                    Arguments.of("PhaseSwitchDTO", PhaseSwitchDTO.class, new PhaseSwitchDTO(GameState.START, GameState.DRAW)),
                    Arguments.of("PlacedTileDTO", PlacedTileDTO.class, new PlacedTileDTO(TileDTO.from(tile), 0, 1)),
                    Arguments.of("GameSettingsDTO", GameSettingsDTO.class, GameSettingsDTO.from(gameSettings)),
                    Arguments.of("GameDTO", GameDTO.class, new GameDTO(
                            new GameStateDTO(GameState.DRAW, player),
                            Map.of(0, Map.of(1, TileDTO.from(tile))),
                            List.of(player)
                    ))
                ),
                deserializableDTOs()
        );
    }

    private static Stream<Arguments> deserializableDTOs() {
        return Stream.of(
                Arguments.of("LobbySettingsDTO", LobbySettingsDTO.class, LobbySettingsDTO.from(lobbySettings)),
                Arguments.of("PlayerIdentificationDTO", PlayerIdentificationDTO.class, new PlayerIdentificationDTO(gameLobby.getId(), player.getId())),
                Arguments.of("MeepleDTO", MeepleDTO.class, new MeepleDTO(player.getId(), tile.getId(), 0, 0))
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("serializableDTOs")
    void testDtoSerialization(String className, Class<?> dtoClass, Object dtoInstance) {
        assertDoesNotThrow(() -> {
            JsonHelper.serializeObject(dtoInstance);
        });
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("deserializableDTOs")
    void testDtoDeserialization(String className, Class<?> dtoClass, Object dtoInstance) {
        assertDoesNotThrow(() -> {
            String serializedObject = JsonHelper.serializeObject(dtoInstance);
            JsonHelper.deserializeObject(serializedObject, dtoClass);
        });
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("serializableDTOs")
    void testEventMessageSerialization(String className, Class<?> dtoClass, Object dtoInstance) {
        assertDoesNotThrow(() -> JsonHelper.serializeObject(new EventMessageDTO<>("testEvent", dtoInstance)));
    }
}
