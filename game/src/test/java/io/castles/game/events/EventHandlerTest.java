package io.castles.game.events;

import io.castles.core.GameMode;
import io.castles.game.GameLobby;
import io.castles.game.GameLobbySettings;
import io.castles.game.Player;
import io.castles.game.Server;
import io.castles.util.CollectingEventConsumer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EventHandlerTest {

    Server server;
    CollectingEventConsumer eventConsumer;

    @BeforeEach
    void setup() {
        server = Server.getInstance();
        eventConsumer = new CollectingEventConsumer();
        server.eventHandler().registerGlobalEventConsumer(eventConsumer);
    }

    @AfterEach
    void reset() {
        eventConsumer.reset();
        server.reset();
    }

    @Test
    void shouldTriggerWhenCreatingALobby() {
        var owner = new Player("owner");
        var gameLobby = server.createGameLobby("Test", owner);

        assertThat(eventConsumer.events()).containsKey(GameEvent.LOBBY_CREATED.name());
        assertThat(eventConsumer.events().get(GameEvent.LOBBY_CREATED.name())).containsExactly(gameLobby.toString());
    }

    @Nested
    class ForGameLobby {

        GameLobby gameLobby;
        Player owner;

        @BeforeEach
        void setup() {
            server.eventHandler().registerGlobalEventConsumer(new GlobalEventConsumer() {
                @Override
                public void onLobbyCreated(GameLobby gameLobby) {
                    server.eventHandler().registerLocalEventConsumer(gameLobby.getId(), eventConsumer);
                }
            });
            owner = new Player("owner");
            gameLobby = server.createGameLobby("Test", owner);
        }

        @Test
        void shouldTriggerPlayerAddedOnCreation() {
            assertThat(eventConsumer.events()).containsKey(GameEvent.PLAYER_ADDED.name());
            assertThat(eventConsumer.events().get(GameEvent.PLAYER_ADDED.name())).containsExactly(owner.toString());
        }

        @Test
        void shouldTriggerWhenPlayerIsAddedToLobby() {
            var player = new Player("P1");
            gameLobby.addPlayer(player);
            assertThat(eventConsumer.events().containsKey(GameEvent.PLAYER_ADDED.name()));
            assertThat(eventConsumer.events().get(GameEvent.PLAYER_ADDED.name()).size()).isEqualTo(2);
            assertThat(eventConsumer.events().get(GameEvent.PLAYER_ADDED.name())).contains(player.toString());
        }

        @Test
        void shouldTriggerWhenPlayerIsRemovedFromLobby() {
            var player = new Player("P1");
            gameLobby.addPlayer(player);
            gameLobby.removePlayer(player);
            assertThat(eventConsumer.events().containsKey(GameEvent.PLAYER_REMOVED.name()));
            assertThat(eventConsumer.events().get(GameEvent.PLAYER_REMOVED.name())).contains(player.toString());
        }

        @Test
        void shouldTriggerSettingsWereChanged() {
            var gameLobbySettings = GameLobbySettings.builder().build();
            gameLobby.changeSettings(gameLobbySettings);
            assertThat(eventConsumer.events().containsKey(GameEvent.SETTINGS_CHANGED.name()));
            assertThat(eventConsumer.events().get(GameEvent.SETTINGS_CHANGED.name())).contains(gameLobbySettings.toString());
        }
    }
}