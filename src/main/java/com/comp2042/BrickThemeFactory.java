package com.comp2042;

import com.comp2042.logic.bricks.BrickGenerator;

/**
 * Abstract factory interface for creating brick themes.
 * <p>
 * Implementations of this interface provide the necessary components to generate bricks
 * and define their visual appearance (color palette).
 * </p>
 */
public interface BrickThemeFactory {

    /**
     * Creates a brick generator for this theme.
     *
     * @return a {@link BrickGenerator} instance.
     */
    BrickGenerator createGenerator();

    /**
     * Creates a color palette for this theme.
     *
     * @return a {@link BrickColorPalette} instance.
     */
    BrickColorPalette createPalette();
}
