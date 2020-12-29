package io.castles.core.mocks;

import io.castles.game.Game;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("test")
@Configuration
public class GameTestConfiguration {
    @Bean
    @Primary
    public Game getMockGame() {
        return Mockito.mock(Game.class);
    }
}
