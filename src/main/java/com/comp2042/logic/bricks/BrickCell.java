package com.comp2042.logic.bricks;

/**
 * Leaf node in the composite. Represents a single occupied cell within a brick
 * rotation.
 */
public final class BrickCell implements BrickComponent {

    private final int x;
    private final int y;
    private final int value;

    /**
     * Creates a new BrickCell.
     *
     * @param x the x coordinate relative to the brick's origin
     * @param y the y coordinate relative to the brick's origin
     * @param value the value associated with this cell (e.g., color index)
     */
    public BrickCell(int x, int y, int value) {
        this.x = x;
        this.y = y;
        this.value = value;
    }

    @Override
    public void forEachCell(CellConsumer consumer) {
        consumer.accept(x, y, value);
    }

    /**
     * Gets the x coordinate.
     *
     * @return the x coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * Gets the y coordinate.
     *
     * @return the y coordinate
    /**
     * Gets the cell value.
     *
     * @return the value
     */
    public int getValue() {
    public int getY() {
        return y;
    }

    public int getValue() {
        return value;
    }
}
