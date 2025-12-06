package com.comp2042;

import com.comp2042.logic.bricks.BrickShape;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * JavaFX implementation of {@link BoardView}. Keeps toolkit specifics outside
 * the controller so alternate renderers can be introduced.
 */
public class JavaFxBoardView implements BoardView {

    private static final int HIDDEN_ROWS = 1;
    private static final Color EMPTY_COLOR = Color.TRANSPARENT;
    private static final int PREVIEW_GRID_SIZE = 4;

    private final GridPane gamePanel;
    private final GridPane brickPanel;
    private final GridPane previewPanel;
    private final int brickSize;

    private Rectangle[][] displayMatrix;
    private Rectangle[][] rectangles;
    private Rectangle[][] previewRectangles;
    private BrickColorPalette palette = new ClassicBrickPalette();

    public JavaFxBoardView(GridPane gamePanel, GridPane brickPanel, GridPane previewPanel, int brickSize) {
        this.gamePanel = gamePanel;
        this.brickPanel = brickPanel;
        this.previewPanel = previewPanel;
        this.brickSize = brickSize;
    }

    @Override
    public void initGameView(int[][] boardMatrix, ViewData brick) {
        displayMatrix = new Rectangle[boardMatrix.length][boardMatrix[0].length];
        for (int i = 0; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                Rectangle rectangle = new Rectangle(brickSize, brickSize);
                rectangle.setFill(EMPTY_COLOR);
                displayMatrix[i][j] = rectangle;
                // Only add visible rows to the game panel (skip hidden rows 0-1)
                if (i >= HIDDEN_ROWS) {
                    gamePanel.add(rectangle, j, i - HIDDEN_ROWS);
                }
            }
        }

        initializeBrickOverlay(brick.getBrickShape());
        paintBrickShape(brick.getBrickShape());
        Point2D boardOrigin = getBoardOrigin();
        double xOffset = brick.getxPosition() * (brickSize + brickPanel.getHgap());
        brickPanel.setLayoutX(boardOrigin.getX() + xOffset);
        brickPanel.setLayoutY(boardOrigin.getY() + (brick.getyPosition() - HIDDEN_ROWS) * brickSize);
        renderNextPreview(brick.getNextBrickShape());
    }

    @Override
    public void refreshBrick(ViewData brick) {
        Point2D boardOrigin = getBoardOrigin();
        double xOffset = brick.getxPosition() * (brickSize + brickPanel.getHgap());
        brickPanel.setLayoutX(boardOrigin.getX() + xOffset);
        brickPanel.setLayoutY(boardOrigin.getY() + (brick.getyPosition() - HIDDEN_ROWS) * brickSize);
        ensureOverlayMatches(brick.getBrickShape());
        paintBrickShape(brick.getBrickShape());
        renderNextPreview(brick.getNextBrickShape());
    }

    @Override
    public void refreshGameBackground(int[][] board) {
        for (int i = 0; i < board.length; i++) {
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
                rectangle.setFill(EMPTY_COLOR);
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

    private void paintBrickShape(BrickShape shape) {
        clearRectangles(rectangles);
        shape.forEachCell((x, y, value) -> setRectangleData(value, rectangles[y][x]));
    }

    @Override
    public void setPalette(BrickColorPalette palette) {
        if (palette != null) {
            this.palette = palette;
        }
    }

    private void renderNextPreview(BrickShape nextShape) {
        if (previewPanel == null) {
            return;
        }
        ensurePreviewGrid();
        clearRectangles(previewRectangles);
        if (nextShape == null) {
            return;
        }
        int offsetX = Math.max(0, (PREVIEW_GRID_SIZE - nextShape.getWidth()) / 2);
        int offsetY = Math.max(0, (PREVIEW_GRID_SIZE - nextShape.getHeight()) / 2);
        nextShape.forEachCell((x, y, value) -> {
            int targetX = x + offsetX;
            int targetY = y + offsetY;
            if (targetX >= 0 && targetX < PREVIEW_GRID_SIZE && targetY >= 0 && targetY < PREVIEW_GRID_SIZE) {
                setRectangleData(value, previewRectangles[targetY][targetX]);
            }
        });
    }

    private void ensurePreviewGrid() {
        if (previewRectangles != null) {
            return;
        }
        previewRectangles = new Rectangle[PREVIEW_GRID_SIZE][PREVIEW_GRID_SIZE];
        previewPanel.getChildren().clear();
        for (int i = 0; i < PREVIEW_GRID_SIZE; i++) {
            for (int j = 0; j < PREVIEW_GRID_SIZE; j++) {
                Rectangle rectangle = new Rectangle(brickSize, brickSize);
                rectangle.setFill(EMPTY_COLOR);
                previewRectangles[i][j] = rectangle;
                previewPanel.add(rectangle, j, i);
            }
        }
    }

    private void clearRectangles(Rectangle[][] matrix) {
        if (matrix == null) {
            return;
        }
        for (Rectangle[] row : matrix) {
            for (Rectangle rectangle : row) {
                rectangle.setFill(EMPTY_COLOR);
            }
        }
    }

    private Point2D getBoardOrigin() {
        Scene scene = gamePanel.getScene();
        if (scene != null) {
            Point2D scenePoint = gamePanel.localToScene(0, 0);
            Node root = scene.getRoot();
            if (root != null) {
                return root.sceneToLocal(scenePoint);
            }
        }
        return new Point2D(gamePanel.getLayoutX(), gamePanel.getLayoutY());
    }
}
