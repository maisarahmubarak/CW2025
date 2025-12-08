package com.comp2042.logic.bricks;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Represents the 'S' shape brick (tetromino).
 * <p>
 * This brick consists of two stacked horizontal dominoes, offset by one block.
 * </p>
 */
final class SBrick implements Brick {

    private final List<BrickShape> shapes;

    /**
     * Constructs a new SBrick and initializes its rotation shapes.
     */
    public SBrick() {
        shapes = Collections.unmodifiableList(Arrays.asList(
                BrickShape.fromMatrix(new int[][]{
                        {0, 0, 0, 0},
                        {0, 5, 5, 0},
                        {5, 5, 0, 0},
                        {0, 0, 0, 0}
                }),
                BrickShape.fromMatrix(new int[][]{
                        {5, 0, 0, 0},
                        {5, 5, 0, 0},
                        {0, 5, 0, 0},
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
