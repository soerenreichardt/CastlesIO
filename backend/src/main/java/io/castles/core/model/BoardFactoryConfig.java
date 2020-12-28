package io.castles.core.model;

import io.castles.core.Board;
import io.castles.core.GameMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

@Configuration
public class BoardFactoryConfig {

    @Bean
    @Scope(SCOPE_PROTOTYPE)
    public Board getBoard(GameMode gameMode) {
        return Board.create(gameMode);
    }
}


