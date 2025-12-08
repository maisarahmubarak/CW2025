package com.comp2042.logic.game;

import com.comp2042.logic.bricks.BrickShape;
import java.util.List;

/**
 * Data transfer object containing information needed to render the game state.
 * <p>
 * Includes the current active brick's shape and position, as well as upcoming bricks.
 * </p>
 */
public final class ViewData {

    private final BrickShape brickShape;
    private final int xPosition;
    private final int yPosition;
    private final List<BrickShape> nextBrickShapes;

    /**
     * Constructs a new ViewData object.
     *
     * @param brickShape the shape of the active brick
     * @param xPosition the x-coordinate of the active brick
     * @param yPosition the y-coordinate of the active brick
     * @param nextBrickShapes list of shapes for the upcoming bricks
     */
    public ViewData(BrickShape brickShape, int xPosition, int yPosition, List<BrickShape> nextBrickShapes) {
        this.brickShape = brickShape;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.nextBrickShapes = nextBrickShapes;
    }

    /**
     * Gets the active brick's shape.
     *
     * @return the {@link BrickShape}
     */
    public BrickShape getBrickShape() {
        return brickShape;
    }

    /**
     * Gets the active brick's x-coordinate.
     *
     * @return the x position
     */
    public int getxPosition() {
        return xPosition;
    }

    /**
     * Gets the active brick's y-coordinate.
     *
     * @return the y position
     */
    public int getyPosition() {
        return yPosition;
    }

    /**
     * Gets the list of upcoming brick shapes.
     *
     * @return a list of {@link BrickShape} objects
     */
    public List<BrickShape> getNextBrickShapes() {
        return nextBrickShapes;
    }
}