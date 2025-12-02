package com.comp2042;

import com.comp2042.logic.bricks.BrickShape;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MatrixOperationsTest {

    @Test
    void intersectDetectsOccupiedCellCollision() {
        int[][] board = {
                {0, 0, 0},
                {0, 0, 0},
                {0, 7, 0}
        };
        BrickShape vertical = BrickShape.fromMatrix(new int[][]{
                {1},
                {1}
        });

        boolean result = MatrixOperations.intersect(board, vertical, 1, 1);

        assertTrue(result, "Intersect must flag collisions with occupied cells");
    }

    @Test
    void intersectDetectsOutOfBoundsMovement() {
        int[][] board = {
                {0, 0, 0},
                {0, 0, 0},
                {0, 0, 0}
        };
        BrickShape horizontal = BrickShape.fromMatrix(new int[][]{
                {1, 1},
                {0, 0}
        });

        boolean leftOutOfBounds = MatrixOperations.intersect(board, horizontal, -1, 0);
        boolean rightOutOfBounds = MatrixOperations.intersect(board, horizontal, 2, 0);

        assertTrue(leftOutOfBounds, "Negative x coordinates must be rejected");
        assertTrue(rightOutOfBounds, "Shapes wider than board must be rejected");
    }

    @Test
    void mergeReturnsNewMatrixWithoutMutatingSource() {
        int[][] board = {
                {0, 0, 0},
                {0, 0, 0}
        };
        BrickShape block = BrickShape.fromMatrix(new int[][]{
                {1, 1}
        });

        int[][] merged = MatrixOperations.merge(board, block, 0, 0);

        assertNotSame(board, merged, "Merge must create a new matrix");
        assertEquals(1, merged[0][0]);
        assertEquals(1, merged[0][1]);
        assertEquals(0, board[0][0], "Original matrix must stay untouched");
    }

    @Test
    void checkRemovingClearsFullRowsAndAwardsBonus() {
        int[][] board = {
                {1, 1, 1},
                {0, 0, 0},
                {1, 1, 1},
                {0, 2, 0}
        };

        ClearRow result = MatrixOperations.checkRemoving(board);

        assertEquals(2, result.getLinesRemoved());
        assertEquals(200, result.getScoreBonus());
        int[][] newMatrix = result.getNewMatrix();
        assertArrayEquals(new int[]{0, 0, 0}, newMatrix[0]);
        assertArrayEquals(new int[]{0, 0, 0}, newMatrix[1]);
        assertArrayEquals(new int[]{0, 0, 0}, newMatrix[2]);
        assertArrayEquals(new int[]{0, 2, 0}, newMatrix[3]);
    }
}
