package com.comp2042.logic.bricks;

import java.util.Collections;
import java.util.List;

/**
 * Represents the 'O' shape brick (tetromino).
 * <p>
 * This brick consists of four blocks forming a square. It does not change shape when rotated.
 * </p>
 */
final class OBrick implements Brick {

    private final List<BrickShape> shapes;

    /**
     * Constructs a new OBrick and initializes its rotation shapes.
     */
    public OBrick() {
        shapes = Collections.singletonList(BrickShape.fromMatrix(new int[][]{
                {0, 0, 0, 0},
                {0, 4, 4, 0},
                {0, 4, 4, 0},
                {0, 0, 0, 0}
        }));
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
