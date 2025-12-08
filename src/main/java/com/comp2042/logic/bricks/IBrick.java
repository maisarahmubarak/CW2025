package com.comp2042.logic.bricks;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Represents the 'I' shape brick (tetromino).
 * <p>
 * This brick consists of four blocks in a straight line.
 * </p>
 */
final class IBrick implements Brick {

    private final List<BrickShape> shapes;

    /**
     * Constructs a new IBrick and initializes its rotation shapes.
     */
    public IBrick() {
        shapes = Collections.unmodifiableList(Arrays.asList(
                BrickShape.fromMatrix(new int[][]{
                {0, 0, 0, 0},
                {1, 1, 1, 1},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        }),
                BrickShape.fromMatrix(new int[][]{
                {0, 1, 0, 0},
                {0, 1, 0, 0},
                {0, 1, 0, 0},
                {0, 1, 0, 0}
        })
        ));
    }

    /**
     * Gets the list of possible shapes (rotations) for this brick.
     *
     * @return a list of {@link BrickShape} objects representing the rotations
     */
    @Override
    public List<BrickShape> getShapes() {
        return shapes;
    }

}
