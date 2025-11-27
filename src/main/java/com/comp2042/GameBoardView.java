package com.comp2042;

import javafx.scene.layout.GridPane;

/**
 * Legacy alias kept for backward compatibility. Prefer {@link JavaFxBoardView}
 * which implements the {@link BoardView} bridge abstraction directly.
 */
@Deprecated
public class GameBoardView extends JavaFxBoardView {

    public GameBoardView(GridPane gamePanel, GridPane brickPanel, int brickSize) {
        super(gamePanel, brickPanel, brickSize);
    }
}
