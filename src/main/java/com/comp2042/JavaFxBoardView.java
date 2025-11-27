package com.comp2042;

import com.comp2042.logic.bricks.BrickShape;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * JavaFX implementation of {@link BoardView}. Keeps toolkit specifics outside
 * the controller so alternate renderers can be introduced.
 */
public class JavaFxBoardView implements BoardView {

    private static final int HIDDEN_ROWS = 2;

    private final GridPane gamePanel;
    private final GridPane brickPanel;
    private final int brickSize;

    private Rectangle[][] displayMatrix;
    private Rectangle[][] rectangles;
    private BrickColorPalette palette = new ClassicBrickPalette();

    public JavaFxBoardView(GridPane gamePanel, GridPane brickPanel, int brickSize) {
        this.gamePanel = gamePanel;
        this.brickPanel = brickPanel;
        this.brickSize = brickSize;
    }

    @Override
    public void initGameView(int[][] boardMatrix, ViewData brick) {
        displayMatrix = new Rectangle[boardMatrix.length][boardMatrix[0].length];
        for (int i = 2; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                Rectangle rectangle = new Rectangle(brickSize, brickSize);
                rectangle.setFill(Color.TRANSPARENT);
                displayMatrix[i][j] = rectangle;
                gamePanel.add(rectangle, j, i - 2);
            }
        }

        initializeBrickOverlay(brick.getBrickShape());
        paintBrickShape(brick.getBrickShape());
        brickPanel.setLayoutX(gamePanel.getLayoutX() + brick.getxPosition() * brickPanel.getVgap() + brick.getxPosition() * brickSize);
        brickPanel.setLayoutY(gamePanel.getLayoutY() + (brick.getyPosition() - HIDDEN_ROWS) * brickSize);
    }

    @Override
    public void refreshBrick(ViewData brick) {
        brickPanel.setLayoutX(gamePanel.getLayoutX() + brick.getxPosition() * brickPanel.getVgap() + brick.getxPosition() * brickSize);
        brickPanel.setLayoutY(gamePanel.getLayoutY() + (brick.getyPosition() - HIDDEN_ROWS) * brickSize);
        ensureOverlayMatches(brick.getBrickShape());
        paintBrickShape(brick.getBrickShape());
    }

    @Override
    public void refreshGameBackground(int[][] board) {
        for (int i = 2; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                setRectangleData(board[i][j], displayMatrix[i][j]);
            }
        }
    }

    private void setRectangleData(int color, Rectangle rectangle) {
        rectangle.setFill(palette.colorFor(color));
        rectangle.setArcHeight(0);
        rectangle.setArcWidth(0);
    }

    private void initializeBrickOverlay(BrickShape shape) {
        brickPanel.getChildren().clear();
        rectangles = new Rectangle[shape.getHeight()][shape.getWidth()];
        for (int i = 0; i < rectangles.length; i++) {
            for (int j = 0; j < rectangles[i].length; j++) {
                Rectangle rectangle = new Rectangle(brickSize, brickSize);
                rectangle.setFill(Color.TRANSPARENT);
                rectangles[i][j] = rectangle;
                brickPanel.add(rectangle, j, i);
            }
        }
    }

    private void ensureOverlayMatches(BrickShape shape) {
        if (rectangles == null || rectangles.length != shape.getHeight() || rectangles[0].length != shape.getWidth()) {
            initializeBrickOverlay(shape);
        }
    }

    private void clearOverlay() {
        if (rectangles == null) {
            return;
        }
        for (Rectangle[] row : rectangles) {
            for (Rectangle rectangle : row) {
                rectangle.setFill(Color.TRANSPARENT);
            }
        }
    }

    private void paintBrickShape(BrickShape shape) {
        clearOverlay();
        shape.forEachCell((x, y, value) -> setRectangleData(value, rectangles[y][x]));
    }

    @Override
    public void setPalette(BrickColorPalette palette) {
        if (palette != null) {
            this.palette = palette;
        }
    }
}
