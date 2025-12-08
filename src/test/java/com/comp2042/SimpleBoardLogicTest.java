package com.comp2042;

import com.comp2042.logic.board.SimpleBoard;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SimpleBoardLogicTest {

    @Test
    void testGameOverDetectedOnSpawn() {
        // 1. Create board (height 20, width 10)
        SimpleBoard board = new SimpleBoard(20, 10);
        
        // 2. Fill the top rows (where spawn happens)
        int[][] matrix = board.getBoardMatrix();
        for (int y = 0; y < matrix.length; y++) {
            for (int x = 0; x < matrix[y].length; x++) {
                matrix[y][x] = 1;
            }
        }

        // 3. Spawn a new brick
        boolean collision = board.createNewBrick();

        // 4. Assert game over
        assertTrue(collision, "Should report collision");
        assertTrue(board.isGameOver(), "Board should be in Game Over state");
    }

    @Test
    void testNewGameResetsGameOverState() {
        SimpleBoard board = new SimpleBoard(20, 10);
        
        // Force game over
        int[][] matrix = board.getBoardMatrix();
        for (int y = 0; y < matrix.length; y++) {
            for (int x = 0; x < matrix[y].length; x++) {
                matrix[y][x] = 1;
            }
        }
        board.createNewBrick();
        assertTrue(board.isGameOver());

        // Reset
        board.newGame();

        // Assert
        assertFalse(board.isGameOver(), "New game should reset Game Over state");
    }

    @Test
    void testLineClearUpdatesScore() {
        // Height 20, Width 10
        SimpleBoard board = new SimpleBoard(20, 10);
        int[][] matrix = board.getBoardMatrix();
        
        // Fill bottom row (index 19)
        for (int x = 0; x < 10; x++) {
            matrix[19][x] = 1;
        }
        
        // Verify initial score
        assertEquals(0, board.getScore().scoreProperty().get());
        
        // Trigger clear
        board.clearRows();
        
        // Expected: 50 * 1^2 + 1 = 51
        assertEquals(51, board.getScore().scoreProperty().get(), "Score should update after single line clear");
    }

    @Test
    void testMultiLineClearBonus() {
        // Height 20, Width 10
        SimpleBoard board = new SimpleBoard(20, 10);
        int[][] matrix = board.getBoardMatrix();
        
        // Fill bottom 4 rows (indices 16, 17, 18, 19)
        for (int y = 16; y < 20; y++) {
            for (int x = 0; x < 10; x++) {
                matrix[y][x] = 1;
            }
        }
        
        // Trigger clear
        board.clearRows();
        
        // Expected: 50 * 4^2 + 4 = 50 * 16 + 4 = 800 + 4 = 804
        assertEquals(804, board.getScore().scoreProperty().get(), "Score should include bonus for multi-line clear");
    }
}
