package io.castles.core.mocks;

import io.castles.core.service.SseEmitterService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("test")
@Configuration
public class SseEmitterServiceTestConfiguration {
    @Bean
    @Primary
    public SseEmitterService getMockSseEmitterService() {
        return Mockito.mock(SseEmitterService.class);
    }
}
