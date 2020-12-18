package io.castles.core;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class BoardFactoryConfig {

    @Bean
    @Scope(value = "prototype")
    public Board getBoard(GameMode gameMode) {
        return Board.create(gameMode);
    }
}


