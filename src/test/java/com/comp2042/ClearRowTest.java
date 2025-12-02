package com.comp2042;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClearRowTest {

    @Test
    void exposesClearedLinesAndScoreBonus() {
        int[][] matrix = new int[][]{
                {0, 0},
                {1, 1}
        };
        ClearRow clearRow = new ClearRow(2, matrix, 200);

        assertEquals(2, clearRow.getLinesRemoved());
        assertEquals(200, clearRow.getScoreBonus());
    }

    @Test
    void getNewMatrixReturnsDefensiveCopy() {
        int[][] matrix = new int[][]{
                {1, 0},
                {0, 1}
        };
        ClearRow clearRow = new ClearRow(1, matrix, 50);

        int[][] returned = clearRow.getNewMatrix();
        returned[0][0] = 9;

        assertNotSame(matrix, returned);
        assertEquals(1, clearRow.getNewMatrix()[0][0], "Internal matrix must not reflect outside mutations");
    }
}
