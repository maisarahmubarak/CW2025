package com.comp2042;

import javafx.scene.paint.Paint;

/**
 * Defines the interface for mapping brick IDs to their corresponding UI colors.
 * <p>
 * Implementations of this interface allow for different color themes to be applied
 * to the game bricks without modifying the core rendering logic.
 * </p>
 */
public interface BrickColorPalette {

    /**
     * Resolves the paint (color) for a given brick ID.
     *
     * @param brickId the unique identifier of the brick type.
     * @return the {@link Paint} object representing the color of the brick.
     *         Returns transparent for ID 0 (empty space).
     */
    Paint colorFor(int brickId);
}
