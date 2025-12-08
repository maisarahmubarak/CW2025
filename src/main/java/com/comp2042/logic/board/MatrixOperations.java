package com.comp2042.logic.board;

import com.comp2042.logic.bricks.BrickShape;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Utility class for performing matrix operations on the game board.
 * <p>
 * Provides static methods for checking intersections, copying matrices,
 * merging shapes into the board, and checking for cleared rows.
 * </p>
 */
public class MatrixOperations {


    //We don't want to instantiate this utility class
    private MatrixOperations(){

    }

    /**
     * Checks if a brick shape intersects with existing blocks in the matrix.
     *
     * @param matrix the board matrix to check against
     * @param shape the brick shape to check
     * @param x the x-coordinate of the shape's position
     * @param y the y-coordinate of the shape's position
     * @return true if the shape intersects with any non-zero block in the matrix or is out of bounds, false otherwise
     */
    public static boolean intersect(final int[][] matrix, final BrickShape shape, int x, int y) {
        final boolean[] conflict = {false};
        shape.forEachCell((cellX, cellY, value) -> {
            if (conflict[0] || value == 0) {
                return;
            }
            int targetX = x + cellX;
            int targetY = y + cellY;
            if (checkOutOfBound(matrix, targetX, targetY)) {
                conflict[0] = true;
            } else if (matrix[targetY][targetX] != 0) {
                conflict[0] = true;
            }
        });
        return conflict[0];
    }

    /**
     * Checks if a target coordinate is out of the matrix bounds.
     *
     * @param matrix the matrix to check bounds against
     * @param targetX the x-coordinate to check
     * @param targetY the y-coordinate to check
     * @return true if the coordinate is outside the matrix dimensions, false otherwise
     */
    private static boolean checkOutOfBound(int[][] matrix, int targetX, int targetY) {
        // Check horizontal bounds (width)
        if (targetX < 0 || targetX >= matrix[0].length) {
            return true;
        }
        // Check vertical bounds (height)
        if (targetY < 0 || targetY >= matrix.length) {
            return true;
        }
        return false;
    }

    /**
     * Creates a deep copy of a 2D integer matrix.
     *
     * @param original the matrix to copy
     * @return a new 2D array containing the same values as the original
     */
    public static int[][] copy(int[][] original) {
        int[][] myInt = new int[original.length][];
        for (int i = 0; i < original.length; i++) {
            int[] aMatrix = original[i];
            int aLength = aMatrix.length;
            myInt[i] = new int[aLength];
            System.arraycopy(aMatrix, 0, myInt[i], 0, aLength);
        }
        return myInt;
    }

    /**
     * Merges a brick shape into a matrix at a specified position.
     *
     * @param filledFields the original matrix (will be copied)
     * @param shape the brick shape to merge
     * @param x the x-coordinate to place the shape
     * @param y the y-coordinate to place the shape
     * @return a new matrix with the shape merged into it
     */
    public static int[][] merge(int[][] filledFields, BrickShape shape, int x, int y) {
        int[][] copy = copy(filledFields);
        shape.forEachCell((cellX, cellY, value) -> {
            int targetX = x + cellX;
            int targetY = y + cellY;
            copy[targetY][targetX] = value;
        });
        return copy;
    }

    /**
     * Checks the matrix for full rows, clears them, and shifts rows down.
     *
     * @param matrix the board matrix to check
     * @return a {@link ClearRow} object containing the results of the operation (lines removed, new matrix, score)
     */
    public static ClearRow checkRemoving(final int[][] matrix) {
        int[][] tmp = new int[matrix.length][matrix[0].length];
        Deque<int[]> newRows = new ArrayDeque<>();
        List<Integer> clearedRows = new ArrayList<>();

        for (int i = 0; i < matrix.length; i++) {
            int[] tmpRow = new int[matrix[i].length];
            boolean rowToClear = true;
            for (int j = 0; j < matrix[0].length; j++) {
                if (matrix[i][j] == 0) {
                    rowToClear = false;
                }
                tmpRow[j] = matrix[i][j];
            }
            if (rowToClear) {
                clearedRows.add(i);
            } else {
                newRows.add(tmpRow);
            }
        }
        for (int i = matrix.length - 1; i >= 0; i--) {
            int[] row = newRows.pollLast();
            if (row != null) {
                tmp[i] = row;
            } else {
                break;
            }
        }
        int scoreBonus = 50 * clearedRows.size() * clearedRows.size();
        int[] clearedArr = clearedRows.stream().mapToInt(Integer::intValue).toArray();
        return new ClearRow(clearedRows.size(), tmp, scoreBonus, clearedArr);
    }
}
