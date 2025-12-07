package com.comp2042;

import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.BrickGenerator;
import com.comp2042.logic.bricks.BrickShape;
import com.comp2042.logic.game.ActiveBrick;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ActiveBrickTest {

    private static final int BOARD_HEIGHT = 24;
    private static final int BOARD_WIDTH = 10;

    @Test
    void moveDownStopsWhenCollisionDetected() {
        int[][] board = emptyBoard();
        ActiveBrick activeBrick = new ActiveBrick(new CyclingBrickGenerator(singleShapeBrick()));
        assertFalse(activeBrick.createNewBrick(board), "Spawn should not collide on an empty board");
        // First downward move is free space.
        assertTrue(activeBrick.moveDown(board));

        int blockingRow = activeBrick.getOffsetY() + activeBrick.getCurrentShape().getHeight();
        int blockingColumn = activeBrick.getOffsetX();
        board[blockingRow][blockingColumn] = 9;

        boolean blocked = activeBrick.moveDown(board);

        assertFalse(blocked, "Collision must stop further downward movement");
        assertEquals(blockingRow - activeBrick.getCurrentShape().getHeight(), activeBrick.getOffsetY());
    }

    @Test
    void moveLeftPreventsExitingBoard() {
        int[][] board = emptyBoard();
        ActiveBrick activeBrick = new ActiveBrick(new CyclingBrickGenerator(singleShapeBrick()));
        activeBrick.createNewBrick(board);

        while (activeBrick.getOffsetX() > 0) {
            assertTrue(activeBrick.moveLeft(board));
        }

        boolean blocked = activeBrick.moveLeft(board);

        assertFalse(blocked);
        assertEquals(0, activeBrick.getOffsetX(), "Piece must stop at the left wall");
    }

    @Test
    void createNewBrickReturnsTrueWhenSpawnCollides() {
        int[][] board = emptyBoard();
        board[1][4] = 8; // default spawn offset is (4, 1)
        ActiveBrick activeBrick = new ActiveBrick(new CyclingBrickGenerator(singleShapeBrick()));

        boolean collided = activeBrick.createNewBrick(board);

        assertTrue(collided, "Spawn overlap should report collision (used for game-over detection)");
    }

    private static int[][] emptyBoard() {
        return new int[BOARD_HEIGHT][BOARD_WIDTH];
    }

    private static Brick singleShapeBrick() {
        BrickShape square = BrickShape.fromMatrix(new int[][]{
                {1, 1},
                {1, 1}
        });
        return new TestBrick(square);
    }

    private static final class CyclingBrickGenerator implements BrickGenerator {
        private final Deque<Brick> bricks;

        private CyclingBrickGenerator(Brick... bricks) {
            this.bricks = new ArrayDeque<>(Arrays.asList(bricks));
        }

        @Override
        public Brick getBrick() {
            Brick brick = bricks.pollFirst();
            if (brick == null) {
                throw new IllegalStateException("No bricks configured");
            }
            bricks.offerLast(brick);
            return brick;
        }

        @Override
        public Brick getNextBrick() {
            Brick brick = bricks.peekFirst();
            if (brick == null) {
                throw new IllegalStateException("No bricks configured");
            }
            return brick;
        }
    }

    private static final class TestBrick implements Brick {
        private final List<BrickShape> shapes;

        private TestBrick(BrickShape... shapes) {
            List<BrickShape> list = new ArrayList<>(Arrays.asList(shapes));
            this.shapes = List.copyOf(list);
        }

        @Override
        public List<BrickShape> getShapes() {
            return shapes;
        }
    }
}
