package com.comp2042.logic.board;

import com.comp2042.GameScore;
import com.comp2042.ViewData;

public interface Board {

    boolean moveBrickDown();

    boolean moveBrickLeft();

    boolean moveBrickRight();

    boolean rotateLeftBrick();

    boolean createNewBrick();

    int[][] getBoardMatrix();

    ViewData getViewData();

    /**
     * Merge (lock) the current piece into the board matrix (merge the active piece into the
     * background).
     */
    void mergeBrickToBackground();

    ClearRow clearRows();

    GameScore getScore();

    void newGame();
    
    void addGarbageLines(int lines);
}