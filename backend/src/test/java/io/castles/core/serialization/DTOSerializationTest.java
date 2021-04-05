package io.castles.core.serialization;

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
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class DTOSerializationTest {


    static Player player = new Player("P1");
    static GameLobbySettings lobbySettings = GameLobbySettings.builder().build();
    static GameSettings gameSettings = GameSettings.from(lobbySettings);
    static Tile tile = Tile.drawStatic(TileContent.GRAS);
    static GameLobby gameLobby = new GameLobby("Test", player, new EventHandler());

    private static Stream<Arguments> dtoClassesAndInstances() {
        return Stream.of(
                Arguments.of("GameStartDTO", GameStartDTO.class, new GameStartDTO(UUID.randomUUID(), List.of(player), player, gameSettings, TileDTO.from(tile))),
                Arguments.of("GameStateDTO", GameStateDTO.class, new GameStateDTO(GameState.START, player)),
                Arguments.of("LobbySettingsDTO", LobbySettingsDTO.class, LobbySettingsDTO.from(lobbySettings)),
                Arguments.of("LobbyStateDTO", LobbyStateDTO.class, LobbyStateDTO.from(gameLobby)),
                Arguments.of("PlayerDTO", PlayerDTO.class, PlayerDTO.from(player)),
                Arguments.of("PlayerIdentificationDTO", PlayerIdentificationDTO.class, new PlayerIdentificationDTO(gameLobby.getId(), player.getId())),
                Arguments.of("PublicLobbyDTO", PublicLobbyDTO.class, PublicLobbyDTO.from(gameLobby)),
                Arguments.of("TileContentMatrixDTO", TileContentMatrixDTO.class, TileContentMatrixDTO.from(tile.<MatrixTileLayout>getTileLayout().getContent())),
                Arguments.of("TileDTO", TileDTO.class, TileDTO.from(tile)),
                Arguments.of("TileLayoutDTO", TileLayoutDTO.class, TileLayoutDTO.from(tile.getTileLayout())),
                Arguments.of("PhaseSwitchDTO", PhaseSwitchDTO.class, new PhaseSwitchDTO(GameState.START, GameState.DRAW))
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("dtoClassesAndInstances")
    void testDtoSerialization(String className, Class<?> dtoClass, Object dtoInstance) {
        assertDoesNotThrow(() -> JsonHelper.serializeObject(dtoInstance));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("dtoClassesAndInstances")
    void testEventMessageSerialization(String className, Class<?> dtoClass, Object dtoInstance) {
        assertDoesNotThrow(() -> JsonHelper.serializeObject(new EventMessageDTO<>("testEvent", dtoInstance)));
    }
}
