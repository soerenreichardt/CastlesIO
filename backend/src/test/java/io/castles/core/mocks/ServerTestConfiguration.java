package io.castles.core.mocks;

import io.castles.game.Server;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("test")
@Configuration
public class ServerTestConfiguration {
    @Bean
    @Primary
    public Server getMockServer() {
        return Mockito.mock(Server.class);
    }
}
