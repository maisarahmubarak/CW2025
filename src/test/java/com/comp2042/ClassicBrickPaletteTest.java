package com.comp2042;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ClassicBrickPaletteTest {

    @Test
    void testPaletteReturnsNonEmptyArray() {
        // Note: ClassicBrickPalette does not expose a getColors() array method.
        // We validate that it returns colors for the standard Tetris brick IDs (1-7).
        ClassicBrickPalette palette = new ClassicBrickPalette();
        boolean hasColors = false;
        for (int i = 1; i <= 7; i++) {
            if (palette.colorFor(i) != null) {
                hasColors = true;
                break;
            }
        }
        assertTrue(hasColors, "Palette must return colors for standard brick IDs");
    }

    @Test
    void testPaletteDoesNotContainNullEntries() {
        ClassicBrickPalette palette = new ClassicBrickPalette();
        // Validate standard IDs 0-7 do not return null
        for (int i = 0; i <= 7; i++) {
            assertNotNull(palette.colorFor(i), "Color for ID " + i + " should not be null");
        }
    }

    @Test
    void testPaletteUsesValidPaintTypes() {
        ClassicBrickPalette palette = new ClassicBrickPalette();
        for (int i = 0; i <= 7; i++) {
            Paint p = palette.colorFor(i);
            assertTrue(p instanceof Color, 
                "Palette entry for ID " + i + " should be a JavaFX Color");
        }
    }
}
