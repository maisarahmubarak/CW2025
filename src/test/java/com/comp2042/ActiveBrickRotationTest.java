package com.comp2042;

import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.BrickGenerator;
import com.comp2042.logic.bricks.BrickShape;
import com.comp2042.logic.game.ActiveBrick;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for verifying the rotation logic of {@link ActiveBrick}.
 * <p>
 * Focuses on wall kicks, boundary checks, and collision detection during rotation.
 * </p>
 */
class ActiveBrickRotationTest {

    private static final int BOARD_WIDTH = 10;
    private static final int BOARD_HEIGHT = 20;

    /**
     * Tests that a brick near the right wall performs a wall kick to rotate successfully.
     */
    @Test
    void testRotateNearRightWallPerformsWallKick() {
        int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        ActiveBrick brick = new ActiveBrick(new FixedBrickGenerator(mockIBrick()));
        brick.createNewBrick(board); 
        // Spawn at x=3 (width 10, brick width 4 -> (10-4)/2 = 3)
        
        // Move to right edge. x=8.
        // Vertical I-piece (shape 0) has occupied cells at col 1.
        // At x=8, occupied board col is 8+1=9. Valid.
        moveBrickTo(brick, board, 8);
        assertEquals(8, brick.getOffsetX());

        // Rotate Left -> Horizontal (shape 1). Occupies cols 0,1,2,3.
        // At x=8, would occupy 8,9,10,11. 10,11 are OOB.
        // Wall kick logic:
        // 0: x=8 -> Fail
        // 1: x=9 -> Fail
        // -1: x=7 -> Occupies 7,8,9,10. Fail (10 OOB)
        // 2: x=10 -> Fail
        // -2: x=6 -> Occupies 6,7,8,9. Success!
        
        boolean rotated = brick.rotateLeft(board);
        
        assertTrue(rotated, "Rotation should succeed via wall kick");
        assertEquals(6, brick.getOffsetX(), "Should kick left by 2 to fit");
        assertHorizontal(brick);
    }

    @Test
    void testRotateBlockedByExistingBrick() {
        int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        ActiveBrick brick = new ActiveBrick(new FixedBrickGenerator(mockIBrick()));
        brick.createNewBrick(board); // x=3
        
        // Vertical I-piece at x=3. Occupies col 4.
        // Rotate -> Horizontal. Occupies cols 3,4,5,6.
        // Row: brickY=1. Horizontal bar is at row 1 of shape. Board row = 1+1=2.
        
        // Place block at (5, 2). This blocks the default rotation.
        board[2][5] = 1;
        
        // Wall kick logic:
        // 0 (x=3): Hits (5,2). Fail.
        // 1 (x=4): Hits (5,2). Fail.
        // -1 (x=2): Hits (5,2). Fail.
        // 2 (x=5): Hits (5,2). Fail.
        // -2 (x=1): Occupies 1,2,3,4. Fits!
        
        boolean rotated = brick.rotateLeft(board);
        
        assertTrue(rotated, "Should rotate with wall kick avoiding the block");
        assertEquals(1, brick.getOffsetX(), "Should kick left by 2 to avoid block");
        assertHorizontal(brick);
    }

    @Test
    void testRotateFullyBlockedFails() {
        int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        ActiveBrick brick = new ActiveBrick(new FixedBrickGenerator(mockIBrick()));
        brick.createNewBrick(board); // x=3
        
        // Block row 2 completely.
        for(int c=0; c<BOARD_WIDTH; c++) {
            board[2][c] = 1;
        }
        
        boolean rotated = brick.rotateLeft(board);
        
        assertFalse(rotated, "Rotation should fail when no space is available");
        assertEquals(3, brick.getOffsetX(), "Position should not change");
        assertVertical(brick);
    }

    private void moveBrickTo(ActiveBrick brick, int[][] board, int targetX) {
        while (brick.getOffsetX() < targetX) {
            brick.moveRight(board);
        }
        while (brick.getOffsetX() > targetX) {
            brick.moveLeft(board);
        }
    }

    private void assertHorizontal(ActiveBrick brick) {
        // Horizontal I-piece has 1 at [1][0]
        assertEquals(1, brick.getCurrentShape().toMatrix()[1][0]);
    }

    private void assertVertical(ActiveBrick brick) {
        // Vertical I-piece has 0 at [1][0] (it's at [1][1])
        assertEquals(0, brick.getCurrentShape().toMatrix()[1][0]);
    }

    private Brick mockIBrick() {
        // Shape 0: Vertical
        // . 1 . .
        // . 1 . .
        // . 1 . .
        // . 1 . .
        int[][] v = new int[4][4];
        v[0][1]=1; v[1][1]=1; v[2][1]=1; v[3][1]=1;
        
        // Shape 1: Horizontal
        // . . . .
        // 1 1 1 1
        // . . . .
        // . . . .
        int[][] h = new int[4][4];
        h[1][0]=1; h[1][1]=1; h[1][2]=1; h[1][3]=1;
        
        return new TestBrick(BrickShape.fromMatrix(v), BrickShape.fromMatrix(h));
    }

    private static class TestBrick implements Brick {
        private final List<BrickShape> shapes;
        TestBrick(BrickShape... shapes) {
            this.shapes = Arrays.asList(shapes);
        }
        @Override
        public List<BrickShape> getShapes() { return shapes; }
    }

    private static class FixedBrickGenerator implements BrickGenerator {
        private final Brick brick;
        FixedBrickGenerator(Brick brick) { this.brick = brick; }
        @Override public Brick getBrick() { return brick; }
        @Override public Brick getNextBrick() { return brick; }
    }
}
