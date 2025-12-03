package com.comp2042;

import com.comp2042.logic.bricks.BrickShape;

public final class NextShapeInfo {

    private final BrickShape shape;
    private final int position;

    public NextShapeInfo(final BrickShape shape, final int position) {
        this.shape = shape;
        this.position = position;
    }

    public BrickShape getShape() {
        return shape;
    }

    public int getPosition() {
        return position;
    }
}
