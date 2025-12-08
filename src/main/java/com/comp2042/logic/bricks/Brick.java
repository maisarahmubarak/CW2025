package com.comp2042.logic.bricks;

import java.util.List;

/**
 * Represents a Tetris brick (tetromino).
 * Defines the contract for retrieving the possible shapes (rotations) of the brick.
 */
public interface Brick {

    /**
     * Gets the list of shapes representing the rotations of this brick.
     *
     * @return a list of BrickShape objects
     */
    List<BrickShape> getShapes();
}
