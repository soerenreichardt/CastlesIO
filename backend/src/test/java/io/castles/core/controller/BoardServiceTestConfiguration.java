package io.castles.core.controller;

import io.castles.core.service.BoardService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("test")
@Configuration
public class BoardServiceTestConfiguration {

    @Bean
    @Primary
    public BoardService getBoardService() {
        return Mockito.mock(BoardService.class);
    }

}
