package com.comp2042.logic.bricks;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Represents the 'L' shape brick (tetromino).
 * <p>
 * This brick consists of three blocks in a line with one block added to the side at the other end (mirror of J).
 * </p>
 */
final class LBrick implements Brick {

    private final List<BrickShape> shapes;

    /**
     * Constructs a new LBrick and initializes its rotation shapes.
     */
    public LBrick() {
        shapes = Collections.unmodifiableList(Arrays.asList(
                BrickShape.fromMatrix(new int[][]{
                        {0, 0, 0, 0},
                        {0, 3, 3, 3},
                        {0, 3, 0, 0},
                        {0, 0, 0, 0}
                }),
                BrickShape.fromMatrix(new int[][]{
                        {0, 0, 0, 0},
                        {0, 3, 3, 0},
                        {0, 0, 3, 0},
                        {0, 0, 3, 0}
                }),
                BrickShape.fromMatrix(new int[][]{
                        {0, 0, 0, 0},
                        {0, 0, 3, 0},
                        {3, 3, 3, 0},
                        {0, 0, 0, 0}
                }),
                BrickShape.fromMatrix(new int[][]{
                        {0, 3, 0, 0},
                        {0, 3, 0, 0},
                        {0, 3, 3, 0},
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
