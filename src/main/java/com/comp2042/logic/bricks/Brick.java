package com.comp2042.logic.bricks;

import java.util.List;

/**
 * Interface defining brick behavior.
 * <p>
 * Represents a Tetris brick (tetromino) and defines the contract for retrieving
 * the possible shapes (rotations) of the brick.
 * </p>
 *
 * @author Maisarah
 * @version 1.0
 */
public interface Brick {

    /**
     * Gets the list of shapes representing the rotations of this brick.
     *
     * @return a list of BrickShape objects
     */
    List<BrickShape> getShapes();
}
