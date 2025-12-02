package com.comp2042;

import com.comp2042.logic.bricks.BrickShape;

public final class ViewData {

    private final BrickShape brickShape;
    private final int xPosition;
    private final int yPosition;
    private final BrickShape nextBrickShape;

    public ViewData(BrickShape brickShape, int xPosition, int yPosition, BrickShape nextBrickShape) {
        this.brickShape = brickShape;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.nextBrickShape = nextBrickShape;
    }

    public BrickShape getBrickShape() {
        return brickShape;
    }

    public int getxPosition() {
        return xPosition;
    }

    public int getyPosition() {
        return yPosition;
    }

    public BrickShape getNextBrickShape() {
        return nextBrickShape;
    }

}
