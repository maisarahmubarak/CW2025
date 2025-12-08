package com.comp2042.logic.bricks;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Represents the 'Z' shape brick (tetromino).
 * <p>
 * This brick consists of two stacked horizontal dominoes, offset by one block (mirror of S).
 * </p>
 */
final class ZBrick implements Brick {

    private final List<BrickShape> shapes;

    /**
     * Constructs a new ZBrick and initializes its rotation shapes.
     */
    public ZBrick() {
        shapes = Collections.unmodifiableList(Arrays.asList(
                BrickShape.fromMatrix(new int[][]{
                        {0, 0, 0, 0},
                        {7, 7, 0, 0},
                        {0, 7, 7, 0},
                        {0, 0, 0, 0}
                }),
                BrickShape.fromMatrix(new int[][]{
                        {0, 7, 0, 0},
                        {7, 7, 0, 0},
                        {7, 0, 0, 0},
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
