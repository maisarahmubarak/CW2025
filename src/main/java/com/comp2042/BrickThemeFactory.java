package com.comp2042;

import com.comp2042.logic.bricks.BrickGenerator;

/**
 * Provides all assets required to render and generate a cohesive brick theme.
 */
public interface BrickThemeFactory {

    BrickGenerator createGenerator();

    BrickColorPalette createPalette();
}
