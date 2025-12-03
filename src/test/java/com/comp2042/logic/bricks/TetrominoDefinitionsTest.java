package com.comp2042.logic.bricks;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TetrominoDefinitionsTest {

    @Test
    void rotationCountsMatchExpectedDefinitions() {
        assertEquals(2, new IBrick().getShapes().size(), "I tetromino should have two rotations");
        assertEquals(4, new JBrick().getShapes().size(), "J tetromino should have four rotations");
        assertEquals(4, new LBrick().getShapes().size(), "L tetromino should have four rotations");
        assertEquals(1, new OBrick().getShapes().size(), "O tetromino should have a single rotation");
        assertEquals(2, new SBrick().getShapes().size(), "S tetromino should have two rotations");
        assertEquals(4, new TBrick().getShapes().size(), "T tetromino should have four rotations");
        assertEquals(2, new ZBrick().getShapes().size(), "Z tetromino should have two rotations");
    }

    @Test
    void eachRotationContainsExactlyFourCells() {
        List<Brick> bricks = List.of(
                new IBrick(),
                new JBrick(),
                new LBrick(),
                new OBrick(),
                new SBrick(),
                new TBrick(),
                new ZBrick()
        );

        for (Brick brick : bricks) {
            for (BrickShape shape : brick.getShapes()) {
                assertEquals(4, countCells(shape), "Every tetromino rotation must contain four blocks");
            }
        }
    }

    private static int countCells(BrickShape shape) {
        final int[] count = {0};
        shape.forEachCell((x, y, value) -> count[0]++);
        return count[0];
    }
}
