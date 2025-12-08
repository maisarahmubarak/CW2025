package com.comp2042.logic.bricks;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Represents the 'J' shape brick (tetromino).
 * <p>
 * This brick consists of three blocks in a line with one block added to the side at one end.
 * </p>
 */
final class JBrick implements Brick {

    private final List<BrickShape> shapes;

    /**
     * Constructs a new JBrick and initializes its rotation shapes.
     */
    public JBrick() {
        shapes = Collections.unmodifiableList(Arrays.asList(
                BrickShape.fromMatrix(new int[][]{
                {0, 0, 0, 0},
                {2, 2, 2, 0},
                {0, 0, 2, 0},
                {0, 0, 0, 0}
        }),
                BrickShape.fromMatrix(new int[][]{
                {0, 0, 0, 0},
                {0, 2, 2, 0},
                {0, 2, 0, 0},
                {0, 2, 0, 0}
        }),
                BrickShape.fromMatrix(new int[][]{
                {0, 0, 0, 0},
                {0, 2, 0, 0},
                {0, 2, 2, 2},
                {0, 0, 0, 0}
        }),
                BrickShape.fromMatrix(new int[][]{
                {0, 0, 2, 0},
                {0, 0, 2, 0},
                {0, 2, 2, 0},
                {0, 0, 0, 0}
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
