package com.comp2042.logic.board;

import com.comp2042.logic.game.GameScore;
import com.comp2042.logic.game.ViewData;

public interface Board {

    boolean moveBrickDown();

    boolean moveBrickLeft();

    void moveBrickRight();

    boolean rotateLeftBrick();

    boolean createNewBrick();

    int[][] getBoardMatrix();

    ViewData getViewData();

    void mergeBrickToBackground();

    ClearRow clearRows();

    GameScore getScore();

    void newGame();

    void addGarbageLines(int lines);
}