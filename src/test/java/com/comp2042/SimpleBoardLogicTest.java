package com.comp2042;

import com.comp2042.logic.board.SimpleBoard;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SimpleBoardLogicTest {

    @Test
    void testGameOverDetectedOnSpawn() {
        // 1. Create board
        SimpleBoard board = new SimpleBoard(10, 20);
        
        // 2. Fill the top rows (where spawn happens)
        // Spawn usually happens at (width-brickWidth)/2, y=1 (HIDDEN_ROWS=1 in ActiveBrick)
        // Let's fill the entire board to be sure.
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
        SimpleBoard board = new SimpleBoard(10, 20);
        
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
}
