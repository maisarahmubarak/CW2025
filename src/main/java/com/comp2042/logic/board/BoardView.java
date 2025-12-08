package com.comp2042.logic.board;

import com.comp2042.BrickColorPalette;
import com.comp2042.logic.game.ViewData;

/**
 * Abstraction for rendering the board and active/next bricks.
 */
public interface BoardView {

    /**
     * Initializes the game view with the initial board state and active brick.
     *
     * @param boardMatrix the initial board matrix
     * @param brick the initial active brick view data
     */
    void initGameView(int[][] boardMatrix, ViewData brick);

    /**
     * Refreshes the background grid based on the board matrix.
     *
     * @param board the current board matrix
     */
    void refreshGameBackground(int[][] board);

    /**
     * Updates the visual representation of the active brick.
    /**
     * Sets the color palette used for rendering bricks.
     *
     * @param palette the BrickColorPalette to use
     */
    void setPalette(BrickColorPalette palette);
     * @param brick the current active brick view data
     */
    void refreshBrick(ViewData brick);

    void setPalette(BrickColorPalette palette);
}