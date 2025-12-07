package com.comp2042;

import com.comp2042.logic.board.SimpleBoard;
import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.BrickGenerator;
import com.comp2042.logic.bricks.BrickShape;
import com.comp2042.logic.game.ViewData;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SimpleBoardTest {

    @Test
    void mergeBrickToBackgroundLocksActiveShape() {
        SimpleBoard board = new SimpleBoard(20, 10, new TestThemeFactory(TestBricks.square()));
        board.createNewBrick();
        ViewData viewBeforeMerge = board.getViewData();

        board.mergeBrickToBackground();

        int[][] matrix = board.getBoardMatrix();
        viewBeforeMerge.getBrickShape().forEachCell((x, y, value) ->
                assertEquals(value, matrix[viewBeforeMerge.getyPosition() + y][viewBeforeMerge.getxPosition() + x])
        );
    }

    @Test
    void newGameResetsBoardAndScore() {
        SimpleBoard board = new SimpleBoard(20, 10, new TestThemeFactory(TestBricks.square()));
        board.createNewBrick();
        board.mergeBrickToBackground();
        board.getScore().add(120);

        board.newGame();

        assertEquals(0, board.getScore().scoreProperty().get());
        int[][] matrix = board.getBoardMatrix();
        for (int[] row : matrix) {
            for (int cell : row) {
                assertEquals(0, cell, "Board must be empty after newGame");
            }
        }
        assertNotNull(board.getViewData().getBrickShape(), "A new active brick must be spawned");
    }

    @Test
    void moveBrickDownStopsWhenBlocked() {
        SimpleBoard board = new SimpleBoard(20, 10, new TestThemeFactory(TestBricks.square()));
        board.createNewBrick();
        ViewData before = board.getViewData();

        int blockY = before.getyPosition() + before.getBrickShape().getHeight();
        int blockX = before.getxPosition();
        board.getBoardMatrix()[blockY][blockX] = 9;

        boolean moved = board.moveBrickDown();

        assertFalse(moved, "Collision should prevent downward move");
        assertEquals(before.getyPosition(), board.getViewData().getyPosition());
    }

    private static final class TestThemeFactory implements BrickThemeFactory {
        private final BrickGenerator generator;

        private TestThemeFactory(Brick brick) {
            this.generator = new FixedBrickGenerator(brick);
        }

        @Override
        public BrickGenerator createGenerator() {
            return generator;
        }

        @Override
        public BrickColorPalette createPalette() {
            return new ClassicBrickPalette();
        }
    }

    private static final class FixedBrickGenerator implements BrickGenerator {
        private final Brick brick;

        private FixedBrickGenerator(Brick brick) {
            this.brick = brick;
        }

        @Override
        public Brick getBrick() {
            return brick;
        }

        @Override
        public Brick getNextBrick() {
            return brick;
        }
    }

    private static final class TestBricks {
        private static Brick square() {
            BrickShape shape = BrickShape.fromMatrix(new int[][]{
                    {1, 1},
                    {1, 1}
            });
            return new TestBrick(List.of(shape));
        }
    }

    private static final class TestBrick implements Brick {
        private final List<BrickShape> shapes;

        private TestBrick(List<BrickShape> shapes) {
            this.shapes = shapes;
        }

        @Override
        public List<BrickShape> getShapes() {
            return shapes;
        }
    }
}
