package com.comp2042.logic.bricks;

/**
 * Factory interface for generating bricks.
 * Provides methods to get a random brick or the next queued brick.
 */
public interface BrickGenerator {

    /**
     * Generates a new random brick.
    /**
     * Retrieves the next brick in the sequence (if a queue is maintained) or generates a new one.
     *
     * @return the next Brick instance
     */
    Brick getNextBrick();
     * @return a new Brick instance
     */
    Brick getBrick();

    Brick getNextBrick();
}
