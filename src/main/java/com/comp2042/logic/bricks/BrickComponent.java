package com.comp2042.logic.bricks;

/**
 * Base component in the composite hierarchy. Both individual cells and whole
 * shapes expose the same iteration contract.
 */
public interface BrickComponent {

    void forEachCell(CellConsumer consumer);
}
