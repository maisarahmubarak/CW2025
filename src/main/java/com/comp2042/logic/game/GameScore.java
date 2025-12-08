package com.comp2042.logic.game;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Manages the game score using JavaFX properties for binding support.
 * <p>
 * Allows the UI to automatically update when the score changes.
 * </p>
 */
public final class GameScore {

    private final IntegerProperty score = new SimpleIntegerProperty(0);

    /**
     * Retrieves the score property for binding.
     *
     * @return the IntegerProperty representing the current score
     */
    public IntegerProperty scoreProperty() {
        return score;
    }

    /**
     * Adds points to the current score.
     *
     * @param i the amount of points to add
     */
    public void add(int i) {
        score.setValue(score.getValue() + i);
    }

    /**
     * Resets the score to zero.
     */
    public void reset() {
        score.setValue(0);
    }
}