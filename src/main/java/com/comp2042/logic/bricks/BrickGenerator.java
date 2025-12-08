package com.comp2042.logic.bricks;

/**
 * Interface for generating new bricks.
 * <p>
 * Provides methods to get a random brick or the next queued brick.
 * </p>
 *
 * @author Maisarah
 * @version 1.0
 */
public interface BrickGenerator {

    /**
     * Generates a new random brick.
     *
     * @return a new Brick instance
     */
    Brick getBrick();

    /**
     * Retrieves the next brick in the sequence (if a queue is maintained) or generates a new one.
     *
     * @return the next Brick instance
     */
    Brick getNextBrick();
}
