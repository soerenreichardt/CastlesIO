package io.castles.core.model;

import io.castles.game.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_SINGLETON;

@Configuration
public class ServerFactoryConfig {

    @Bean
    @Scope(SCOPE_SINGLETON)
    public Server getServer() {
        return Server.getInstance();
    }
}
