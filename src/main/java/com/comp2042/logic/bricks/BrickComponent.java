package com.comp2042.logic.bricks;

/**
 * Base component in the composite hierarchy. Both individual cells and whole
 * shapes expose the same iteration contract.
 */
public interface BrickComponent {

    /**
     * Iterates over each cell in this component.
     *
     * @param consumer the action to perform on each cell
     */
    void forEachCell(CellConsumer consumer);
}
