package com.comp2042.logic.bricks;

/**
 * Functional interface used by the brick composite to iterate over each cell.
 */
@FunctionalInterface
public interface CellConsumer {
    /**
     * Performs an operation on a cell.
     *
     * @param x the x coordinate of the cell
     * @param y the y coordinate of the cell
     * @param value the value of the cell
     */
    void accept(int x, int y, int value);
}
