package io.castles.core.service;

import io.castles.core.Board;
import io.castles.core.GameMode;
import org.springframework.stereotype.Service;

@Service
public class BoardService {

    private Board board;

    public Board createBoard(GameMode gameMode) {
        this.board = Board.create(gameMode);
        return board;
    }

    public Board getBoard() {
        return board;
    }
}
