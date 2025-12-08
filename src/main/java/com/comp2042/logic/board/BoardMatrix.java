package com.comp2042.logic.board;

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

    public void addGarbageLines(int lines) {
        if (lines <= 0) {
            return;
        }
        for (int i = 0; i < lines; i++) {
            // push everything up by 1 row (remove top row) and create a garbage row at bottom
            int[][] newMatrix = new int[width][height];
            for (int x = 0; x < width; x++) {
                for (int y = 1; y < height; y++) {
                    newMatrix[x][y - 1] = matrix[x][y];
                }
            }
            // bottom row filled with color code 1, leaving one random hole
            int hole = (int) (Math.random() * width);
            for (int x = 0; x < width; x++) {
                newMatrix[x][height - 1] = (x == hole) ? 0 : 1;
            }
            matrix = newMatrix;
        }
    }
}