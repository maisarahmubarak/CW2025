package com.comp2042;

import com.comp2042.logic.bricks.Brick;
import java.awt.Point;

/**
 * Encapsulates the active falling brick: rotator and current offset.
 * Brick generation and preview are moved to {@link BrickPreview}.
 */
public class ActiveBrick {

    private final BrickRotator brickRotator;
    private final BrickPreview preview;
    private Point currentOffset;

    public ActiveBrick() {
        this.preview = new BrickPreview();
        this.brickRotator = new BrickRotator();
        this.currentOffset = new Point(0, 0);
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
        boolean conflict = MatrixOperations.intersect(currentMatrix, nextShape.getShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
        if (conflict) {
            return false;
        } else {
            brickRotator.setCurrentShape(nextShape.getPosition());
            return true;
        }
    }

    public boolean createNewBrick(int[][] boardMatrix) {
        Brick currentBrick = preview.consumeNext();
        brickRotator.setBrick(currentBrick);
        currentOffset = new Point(4, 1);
        return MatrixOperations.intersect(boardMatrix, brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
    }

    public int[][] getCurrentShape() {
        return brickRotator.getCurrentShape();
    }

    public int getOffsetX() {
        return (int) currentOffset.getX();
    }

    public int getOffsetY() {
        return (int) currentOffset.getY();
    }

    public int[][] getNextPreview() {
        return preview.peekNextPreview();
    }
}
