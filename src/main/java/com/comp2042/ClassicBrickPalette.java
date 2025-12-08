package com.comp2042;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 * A concrete implementation of {@link BrickColorPalette} providing the classic Tetris colors.
 * <p>
 * Maps standard brick IDs to their traditional colors (e.g., Cyan for I, Yellow for O, Purple for T).
 * </p>
 */
public class ClassicBrickPalette implements BrickColorPalette {

    /**
     * Returns the color associated with the given brick ID.
     *
     * @param brickId the ID of the brick.
     * @return the {@link Paint} color for the brick.
     */
    @Override
    public Paint colorFor(int brickId) {
        switch (brickId) {
            case 0:
                return Color.TRANSPARENT;
            case 1: // I
                return Color.web("#00E5FF");
            case 2: // J
                return Color.web("#004CFF");
            case 3: // L
                return Color.web("#FF9100");
            case 4: // O
                return Color.web("#F9FF00");
            case 5: // S
                return Color.web("#76FF03");
            case 6: // T
                return Color.web("#C400FF");
            case 7: // Z
                return Color.web("#FF1744");
            default:
                return Color.WHITE;
        }
    }
}
