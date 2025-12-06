package com.comp2042;

import com.comp2042.logic.bricks.BrickShape;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.geometry.Pos;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
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
    private static final int PREVIEW_COUNT = 3;

    private final GridPane gamePanel;
    private final GridPane brickPanel;
    private final GridPane previewPanel;
    private final int brickSize;

    private Rectangle[][] displayMatrix;
    private Rectangle[][] rectangles;
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
        renderNextPreviewList(brick.getNextBrickShapes());
    }

    @Override
    public void refreshBrick(ViewData brick) {
        Point2D boardOrigin = getBoardOrigin();
        double xOffset = brick.getxPosition() * (brickSize + brickPanel.getHgap());
        brickPanel.setLayoutX(boardOrigin.getX() + xOffset);
        brickPanel.setLayoutY(boardOrigin.getY() + (brick.getyPosition() - HIDDEN_ROWS) * brickSize);
        ensureOverlayMatches(brick.getBrickShape());
        paintBrickShape(brick.getBrickShape());
        renderNextPreviewList(brick.getNextBrickShapes());
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

    private void renderNextPreviewList(java.util.List<BrickShape> nextShapes) {
        if (previewPanel == null) {
            return;
        }
        previewPanel.getChildren().clear();
        previewPanel.setAlignment(Pos.CENTER);

        if (nextShapes == null || nextShapes.isEmpty()) {
            return;
        }

        for (int k = 0; k < Math.min(nextShapes.size(), PREVIEW_COUNT); k++) {
            BrickShape nextShape = nextShapes.get(k);
            if (nextShape == null) continue;

            // Create a container for this shape
            GridPane shapeContainer = new GridPane();
            shapeContainer.setHgap(1);
            shapeContainer.setVgap(1);
            shapeContainer.setAlignment(Pos.CENTER);

            nextShape.forEachCell((x, y, value) -> {
                Rectangle rectangle = new Rectangle(brickSize, brickSize);
                rectangle.setFill(palette.colorFor(value));
                rectangle.setArcHeight(0);
                rectangle.setArcWidth(0);
                shapeContainer.add(rectangle, x, y);
            });

            // Wrap in a StackPane to ensure centering within the slot
            StackPane slot = new StackPane(shapeContainer);
            slot.setPrefSize(brickSize * 4, brickSize * 4);
            slot.setAlignment(Pos.CENTER);

            previewPanel.add(slot, 0, k);
            GridPane.setHalignment(slot, HPos.CENTER);
            GridPane.setValignment(slot, VPos.CENTER);
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

    /**
     * Returns the position of the top-left corner of the cell (column, row) in Scene coordinates.
     * Row indexing is the same as the internal matrix (includes hidden rows), so caller must
     * account for HIDDEN_ROWS when placing visuals that only show visible rows.
     */
    public Point2D getCellScenePosition(int column, int row) {
        // Convert grid (column, row) into scene coordinates using the visible board origin
        javafx.geometry.Point2D sceneOrigin = gamePanel.localToScene(0, 0);
        double x = sceneOrigin.getX() + column * (brickSize + gamePanel.getHgap());
        double y = sceneOrigin.getY() + (row - HIDDEN_ROWS) * brickSize;
        return new Point2D(x, y);
    }
}
