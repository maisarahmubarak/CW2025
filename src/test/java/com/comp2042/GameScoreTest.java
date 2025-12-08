package com.comp2042;

import com.comp2042.logic.game.GameScore;
import javafx.beans.property.IntegerProperty;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Unit tests for the {@link GameScore} class.
 * <p>
 * Verifies score accumulation, resetting, and property binding support.
 * </p>
 */
class GameScoreTest {

    /**
     * Verifies that adding points correctly updates the score property.
     */
    @Test
    void addAccumulatesScore() {
        GameScore gameScore = new GameScore();

        gameScore.add(5);
        gameScore.add(15);

        assertEquals(20, gameScore.scoreProperty().get());
    }

    @Test
    void resetClearsScore() {
        GameScore gameScore = new GameScore();
        gameScore.add(42);

        gameScore.reset();

        assertEquals(0, gameScore.scoreProperty().get());
    }

    @Test
    void scorePropertyIsStableReference() {
        GameScore gameScore = new GameScore();

        IntegerProperty first = gameScore.scoreProperty();
        IntegerProperty second = gameScore.scoreProperty();

        assertSame(first, second, "scoreProperty should always return the same observable for binding");
    }
}
