package com.comp2042.logic.board;

import com.comp2042.BrickColorPalette;
import com.comp2042.ViewData;

/**
 * Abstraction for rendering the board and active/next bricks.
 */
public interface BoardView {

    void initGameView(int[][] boardMatrix, ViewData brick);

    void refreshGameBackground(int[][] board);

    void refreshBrick(ViewData brick);

    void setPalette(BrickColorPalette palette);
}
