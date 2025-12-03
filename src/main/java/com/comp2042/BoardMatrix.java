package com.comp2042;

import com.comp2042.logic.bricks.BrickShape;

/**
 * Holds the board matrix and operations that mutate it (merge, clearRows, reset).
 * Extracted from SimpleBoard to improve single responsibility.
 */
public class BoardMatrix {

    private final int width;
    private final int height;
    private int[][] matrix;

    public BoardMatrix(int width, int height) {
        this.width = width;
        this.height = height;
        this.matrix = new int[width][height];
    }

    public int[][] getBoardMatrix() {
        return matrix;
    }

    public void merge(BrickShape brick, int x, int y) {
        this.matrix = MatrixOperations.merge(this.matrix, brick, x, y);
    }

    public ClearRow clearRows() {
        ClearRow clearRow = MatrixOperations.checkRemoving(this.matrix);
        this.matrix = clearRow.getNewMatrix();
        return clearRow;
    }

    public void reset() {
        this.matrix = new int[width][height];
    }
}
