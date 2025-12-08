package com.comp2042.ui;

import javafx.scene.layout.GridPane;

/**
 * Legacy alias kept for backward compatibility. Prefer {@link JavaFxBoardView}
 * which implements the {@link com.comp2042.logic.board.BoardView} bridge abstraction directly.
 */
@Deprecated
public class GameBoardView extends JavaFxBoardView {

    /**
     * Creates a new GameBoardView.
     *
     * @param gamePanel the main game grid
     * @param brickPanel the panel for the active brick (unused in new logic)
     * @param previewPanel the panel for the next brick preview
     * @param brickSize the size of each brick cell in pixels
     */
    public GameBoardView(GridPane gamePanel, GridPane brickPanel, GridPane previewPanel, int brickSize) {
        super(gamePanel, brickPanel, previewPanel, brickSize);
    }
}
