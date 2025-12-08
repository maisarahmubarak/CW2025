package com.comp2042.ui;

import javafx.scene.layout.GridPane;

/**
 * Legacy alias kept for backward compatibility. Prefer {@link JavaFxBoardView}
 * which implements the {@link BoardView} bridge abstraction directly.
 */
@Deprecated
public class GameBoardView extends JavaFxBoardView {

    public GameBoardView(GridPane gamePanel, GridPane brickPanel, GridPane previewPanel, int brickSize) {
        super(gamePanel, brickPanel, previewPanel, brickSize);
    }
}
