package com.comp2042.logic.board;

import com.comp2042.logic.bricks.BrickShape;

/**
 * 2D matrix representation of the game board.
 * <p>
 * Holds the board matrix and operations that mutate it (merge, clearRows, reset).
 * Extracted from SimpleBoard to improve single responsibility.
 * </p>
 *
 * @author Maisarah
 * @version 1.0
 */
public class BoardMatrix {

    private final int width;
    private final int height;
    private int[][] matrix;

    /**
     * Creates a new BoardMatrix with the specified dimensions.
     *
     * @param width the width of the board (number of columns)
     * @param height the height of the board (number of rows)
     */
    public BoardMatrix(int width, int height) {
        this.width = width;
        this.height = height;
        this.matrix = new int[width][height];
    }

    /**
     * Gets the current state of the board matrix.
     *
     * @return the 2D integer array representing the board
     */
    public int[][] getBoardMatrix() {
        return matrix;
    }

    /**
     * Merges a brick shape into the board matrix at the specified position.
     *
     * @param brick the brick shape to merge
     * @param x the x coordinate (column)
     * @param y the y coordinate (row)
     */
    public void merge(BrickShape brick, int x, int y) {
        this.matrix = MatrixOperations.merge(this.matrix, brick, x, y);
    }

    /**
     * Checks for and clears any full rows in the matrix.
     *
     * @return a ClearRow object containing details about the cleared rows and the new matrix state
     */
    public ClearRow clearRows() {
        ClearRow clearRow = MatrixOperations.checkRemoving(this.matrix);
        this.matrix = clearRow.getNewMatrix();
        return clearRow;
    }

    /**
     * Resets the board matrix to an empty state.
     */
    public void reset() {
        this.matrix = new int[width][height];
    }

    /**
     * Adds garbage lines to the bottom of the board, pushing existing blocks up.
     *
     * @param lines the number of garbage lines to add
     */
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
                // fill the bottom row with garbage (value 8 to distinguish from normal bricks)
                newMatrix[x][height - 1] = 8;
            }
            // leave a random gap in the garbage row
            int gap = (int) (Math.random() * width);
            newMatrix[gap][height - 1] = 0;
            this.matrix = newMatrix;
        }
    }
}
