package com.comp2042;

import javafx.scene.paint.Paint;

/**
 * Describes how brick ids map to UI colors so themes can swap palettes without
 * editing rendering code.
 */
public interface BrickColorPalette {

    /**
     * Resolve the paint for a given brick id. 0 should remain transparent.
     */
    Paint colorFor(int brickId);
}
