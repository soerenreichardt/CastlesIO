package io.castles.core.mocks;

import io.castles.game.Server;
import org.springframework.context.annotation.*;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

@Profile("test")
@Configuration
public class ServerTestConfiguration {
    @Bean
    @Primary
    @Scope(SCOPE_PROTOTYPE)
    public Server getServerInstance() {
        Server server = Server.getInstance();
        server.reset();
        return server;
    }
}
