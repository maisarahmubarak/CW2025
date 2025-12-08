package com.comp2042;

import com.comp2042.logic.bricks.BrickGenerator;
import com.comp2042.logic.bricks.RandomBrickGenerator;

/**
 * A concrete implementation of {@link BrickThemeFactory} for the classic Tetris theme.
 * <p>
 * This factory produces a {@link RandomBrickGenerator} and a {@link ClassicBrickPalette}.
 * </p>
 */
public class ClassicBrickFactory implements BrickThemeFactory {

    /**
     * Creates a random brick generator.
     *
     * @return a new {@link RandomBrickGenerator}.
     */
    @Override
    public BrickGenerator createGenerator() {
        return new RandomBrickGenerator();
    }

    /**
     * Creates the classic color palette.
     *
     * @return a new {@link ClassicBrickPalette}.
     */
    @Override
    public BrickColorPalette createPalette() {
        return new ClassicBrickPalette();
    }
}
