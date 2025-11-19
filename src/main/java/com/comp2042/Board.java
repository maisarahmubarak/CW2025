package com.comp2042;

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

    Score getScore();

    void newGame();
}
