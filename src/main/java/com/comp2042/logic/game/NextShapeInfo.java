package com.comp2042.logic.game;

import com.comp2042.logic.bricks.BrickShape;

/**
 * Holds information about a future brick shape to be displayed in the preview area.
 */
public final class NextShapeInfo {

    private final BrickShape shape;
    private final int position;

    /**
     * Constructs a new NextShapeInfo object.
     *
     * @param shape the shape of the next brick
     * @param position the index/position in the preview queue
     */
    public NextShapeInfo(final BrickShape shape, final int position) {
        this.shape = shape;
        this.position = position;
    }

    /**
     * Gets the brick shape.
     *
     * @return the {@link BrickShape}
     */
    public BrickShape getShape() {
        return shape;
    }

    /**
     * Gets the position index.
     *
     * @return the position index
     */
    public int getPosition() {
        return position;
    }
}