package io.castles.core.controller;

import io.castles.core.GameMode;
import io.castles.core.Tile;
import io.castles.core.service.BoardService;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/board")
public class BoardController {

    @Autowired
    BeanFactory beanFactory;

    @Autowired
    BoardService boardService;

    GameMode gameMode;

    public BoardController() {
        this.gameMode = GameMode.DEBUG;
    }

    @PostMapping("/new")
    @ResponseStatus(HttpStatus.OK)
    String createBoard(@RequestParam("game_mode") String gameMode) {
        boardService.createBoard(GameMode.valueOf(gameMode));
        return "Board created";
    }

    @GetMapping("/new_tile")
    @ResponseBody
    Tile getNextTile() {
        return boardService.getBoard().getNewTile();
    }

    @GetMapping(value = "/tile")
    @ResponseBody
    Tile getTile(@RequestParam("x") int x, @RequestParam("y") int y) {
        return boardService.getBoard().getTile(x, y);
    }
}
