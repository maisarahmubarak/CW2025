package com.comp2042;

import com.comp2042.logic.board.BoardMatrix;
import com.comp2042.logic.board.ClearRow;
import com.comp2042.logic.bricks.BrickShape;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardMatrixTest {

    @Test
    void mergePersistsShapeIntoInternalMatrix() {
        BoardMatrix boardMatrix = new BoardMatrix(4, 4);
        BrickShape cell = BrickShape.fromMatrix(new int[][]{{5}});

        boardMatrix.merge(cell, 1, 2);

        int[][] matrix = boardMatrix.getBoardMatrix();
        assertEquals(5, matrix[2][1]);
    }

    @Test
    void clearRowsMutatesMatrixToResultingState() {
        BoardMatrix boardMatrix = new BoardMatrix(4, 4);
        int[][] matrix = boardMatrix.getBoardMatrix();
        for (int x = 0; x < matrix[0].length; x++) {
            matrix[0][x] = 1; // make first row full
        }
        matrix[1][1] = 9; // leave second row partially filled so it remains

        ClearRow clearRow = boardMatrix.clearRows();

        assertEquals(1, clearRow.getLinesRemoved());
        int[][] expected = clearRow.getNewMatrix();
        assertArrayEquals(expected[0], boardMatrix.getBoardMatrix()[0]);
        assertArrayEquals(expected[1], boardMatrix.getBoardMatrix()[1]);
        assertArrayEquals(expected[2], boardMatrix.getBoardMatrix()[2]);
        assertArrayEquals(expected[3], boardMatrix.getBoardMatrix()[3]);
    }

    @Test
    void resetProducesEmptyMatrix() {
        BoardMatrix boardMatrix = new BoardMatrix(4, 4);
        boardMatrix.getBoardMatrix()[3][3] = 8;

        boardMatrix.reset();

        int[][] matrix = boardMatrix.getBoardMatrix();
        for (int y = 0; y < matrix.length; y++) {
            for (int x = 0; x < matrix[y].length; x++) {
                assertEquals(0, matrix[y][x], "All cells must be zero after reset");
            }
        }
    }
}
