package com.comp2042.logic.game;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * Global settings for the game application.
 * <p>
 * Manages application-wide preferences such as brightness and volume.
 * Uses JavaFX properties to allow listeners to react to changes.
 * </p>
 */
public class GameSettings {
    private static final DoubleProperty brightness = new SimpleDoubleProperty(1.0);
    private static final DoubleProperty volume = new SimpleDoubleProperty(50.0);

    /**
     * Retrieves the brightness property.
     *
     * @return the DoubleProperty for brightness
     */
    public static DoubleProperty brightnessProperty() { return brightness; }

    /**
     * Retrieves the volume property.
     *
     * @return the DoubleProperty for volume
     */
    public static DoubleProperty volumeProperty() { return volume; }

    /**
     * Gets the current brightness value.
     *
     * @return the brightness value (0.0 to 1.0)
     */
    public static double getBrightness() { return brightness.get(); }

    /**
     * Sets the brightness value.
     * The value is clamped between 0.0 and 1.0.
     *
     * @param value the new brightness value
     */
    public static void setBrightness(double value) {
        // Clamp between 0.0 and 1.0
        double clamped = Math.max(0.0, Math.min(1.0, value));
        brightness.set(clamped);
    }

    /**
     * Gets the current volume value.
     *
     * @return the volume value (0.0 to 100.0)
     */
    public static double getVolume() { return volume.get(); }

    /**
     * Sets the volume value.
     * The value is clamped between 0.0 and 100.0.
     *
     * @param value the new volume value
     */
    public static void setVolume(double value) {
        // Clamp between 0.0 and 100.0
        double clamped = Math.max(0.0, Math.min(100.0, value));
        volume.set(clamped);
    }
}