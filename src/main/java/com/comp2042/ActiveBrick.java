package com.comp2042;

import com.comp2042.logic.board.MatrixOperations;
import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.BrickGenerator;
import com.comp2042.logic.bricks.BrickShape;
import com.comp2042.logic.game.NextShapeInfo;
import java.awt.Point;

/**
 * Encapsulates the active falling brick: rotator and current offset.
 * Brick generation and preview are moved to {@link BrickProvider}.
 */
public class ActiveBrick {

    private final BrickRotator brickRotator;
    private final BrickProvider preview;
    private static final int HIDDEN_ROWS = 1;
    private Point currentOffset;

    public ActiveBrick(BrickGenerator generator) {
        this.preview = new BrickProvider(generator);
        this.brickRotator = new BrickRotator();
        this.currentOffset = new Point(0, 0);
    }

    public ActiveBrick() {
        this(new ClassicBrickFactory().createGenerator());
    }

    public boolean moveDown(int[][] boardMatrix) {
        int[][] currentMatrix = MatrixOperations.copy(boardMatrix);
        Point p = new Point(currentOffset);
        p.translate(0, 1);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }

    public boolean moveLeft(int[][] boardMatrix) {
        int[][] currentMatrix = MatrixOperations.copy(boardMatrix);
        Point p = new Point(currentOffset);
        p.translate(-1, 0);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }

    public boolean moveRight(int[][] boardMatrix) {
        int[][] currentMatrix = MatrixOperations.copy(boardMatrix);
        Point p = new Point(currentOffset);
        p.translate(1, 0);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }

    public boolean rotateLeft(int[][] boardMatrix) {
        int[][] currentMatrix = MatrixOperations.copy(boardMatrix);
        NextShapeInfo nextShape = brickRotator.getNextShape();
        int[] horizontalOffsets = {0, 1, -1, 2, -2};
        for (int offset : horizontalOffsets) {
            Point candidate = new Point(currentOffset);
            candidate.translate(offset, 0);
            if (!MatrixOperations.intersect(currentMatrix, nextShape.getShape(), (int) candidate.getX(), (int) candidate.getY())) {
                currentOffset = candidate;
                brickRotator.setCurrentShape(nextShape.getPosition());
                return true;
            }
        }
        return false;
    }

    public boolean createNewBrick(int[][] boardMatrix) {
        Brick currentBrick = preview.consumeNext();
        brickRotator.setBrick(currentBrick);
        // Spawn centered horizontally at top (row 0-1, hidden rows)
        int boardWidth = boardMatrix[0].length;
        int brickWidth = brickRotator.getCurrentShape().getWidth();
        int spawnX = (boardWidth - brickWidth) / 2;
        currentOffset = new Point(spawnX, HIDDEN_ROWS);
        return MatrixOperations.intersect(boardMatrix, brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
    }

    public BrickShape getCurrentShape() {
        return brickRotator.getCurrentShape();
    }

    public int getOffsetX() {
        return (int) currentOffset.getX();
    }

    public int getOffsetY() {
        return (int) currentOffset.getY();
    }

    public BrickShape getNextPreview() {
        return preview.peekNextPreview();
    }

    public java.util.List<BrickShape> getNextPreviews(int count) {
        return preview.peekNextPreviews(count);
    }
}
