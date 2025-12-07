package com.comp2042.logic.game;

import com.comp2042.logic.bricks.BrickShape;
import java.util.List;

public final class ViewData {

    private final BrickShape brickShape;
    private final int xPosition;
    private final int yPosition;
    private final List<BrickShape> nextBrickShapes;

    public ViewData(BrickShape brickShape, int xPosition, int yPosition, List<BrickShape> nextBrickShapes) {
        this.brickShape = brickShape;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.nextBrickShapes = nextBrickShapes;
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

    public List<BrickShape> getNextBrickShapes() {
        return nextBrickShapes;
    }
}