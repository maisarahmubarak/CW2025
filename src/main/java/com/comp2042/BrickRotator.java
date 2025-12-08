package com.comp2042;

import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.BrickShape;
import com.comp2042.logic.game.NextShapeInfo;

/**
 * Handles the rotation logic for a brick.
 * <p>
 * This class maintains the current rotation state of a brick and calculates the next rotation state.
 * </p>
 */
public class BrickRotator {

    private Brick brick;
    private int currentShape = 0;

    /**
     * Creates a new BrickRotator.
     */
    public BrickRotator() {
    }

    /**
     * Calculates the next rotation shape for the current brick.
     *
     * @return a {@link NextShapeInfo} object containing the next shape matrix and its index.
     */
    public NextShapeInfo getNextShape() {
        int nextShape = currentShape;
        nextShape = (++nextShape) % brick.getShapes().size();
        return new NextShapeInfo(brick.getShapes().get(nextShape), nextShape);
    }

    /**
     * Retrieves the current shape matrix of the brick.
     *
     * @return the {@link BrickShape} representing the current rotation.
     */
    public BrickShape getCurrentShape() {
        return brick.getShapes().get(currentShape);
    }

    /**
     * Sets the current rotation index of the brick.
     *
     * @param currentShape the index of the shape in the brick's rotation list.
     */
    public void setCurrentShape(int currentShape) {
        this.currentShape = currentShape;
    }

    /**
     * Sets the brick to be rotated.
     * <p>
     * Resets the rotation index to 0 (default orientation).
     * </p>
     *
     * @param brick the {@link Brick} object to control.
     */
    public void setBrick(Brick brick) {
        this.brick = brick;
        currentShape = 0;
    }


}
