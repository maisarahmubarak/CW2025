package com.comp2042;

import com.comp2042.logic.bricks.BrickGenerator;
import com.comp2042.logic.bricks.RandomBrickGenerator;

/**
 * Default theme factory producing the existing random generator and color palette.
 */
public class ClassicBrickFactory implements BrickThemeFactory {

    @Override
    public BrickGenerator createGenerator() {
        return new RandomBrickGenerator();
    }

    @Override
    public BrickColorPalette createPalette() {
        return new ClassicBrickPalette();
    }
}
