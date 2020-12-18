package io.castles.core.controller;

import io.castles.core.Board;
import io.castles.core.GameMode;
import io.castles.core.Tile;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/board")
public class BoardController {

    @Autowired
    BeanFactory beanFactory;

    GameMode gameMode;

    public BoardController() {
        this.gameMode = GameMode.DEBUG;
    }

    @GetMapping("/new_tile")
    Tile getNextTile() {
        return getBoard().getNewTile();
    }

    private Board getBoard() {
        return beanFactory.getBean(Board.class, gameMode);
    }
}
