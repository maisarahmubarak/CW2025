package com.comp2042.logic.bricks;

/**
 * Functional interface used by the brick composite to iterate over each cell.
 */
@FunctionalInterface
public interface CellConsumer {
    void accept(int x, int y, int value);
}
