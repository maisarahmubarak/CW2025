package com.comp2042.logic.bricks;

/**
 * Leaf node in the composite. Represents a single occupied cell within a brick
 * rotation.
 */
public final class BrickCell implements BrickComponent {

    private final int x;
    private final int y;
    private final int value;

    public BrickCell(int x, int y, int value) {
        this.x = x;
        this.y = y;
        this.value = value;
    }

    @Override
    public void forEachCell(CellConsumer consumer) {
        consumer.accept(x, y, value);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getValue() {
        return value;
    }
}
