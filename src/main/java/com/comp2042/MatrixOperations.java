package com.comp2042;

import com.comp2042.logic.bricks.BrickShape;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class MatrixOperations {


    //We don't want to instantiate this utility class
    private MatrixOperations(){

    }

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

    public static int[][] merge(int[][] filledFields, BrickShape shape, int x, int y) {
        int[][] copy = copy(filledFields);
        shape.forEachCell((cellX, cellY, value) -> {
            int targetX = x + cellX;
            int targetY = y + cellY;
            copy[targetY][targetX] = value;
        });
        return copy;
    }

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
        return new ClearRow(clearedRows.size(), tmp, scoreBonus);
    }
}
