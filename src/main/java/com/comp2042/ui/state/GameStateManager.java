package com.comp2042.ui.state;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * Manages game state properties (pause, game over).
 * Provides centralized access to game state for all controllers.
 */
public class GameStateManager {
    
    private final BooleanProperty isPause = new SimpleBooleanProperty(false);
    private final BooleanProperty isGameOver = new SimpleBooleanProperty(false);
    
    /**
     * Gets the pause property.
     */
    public BooleanProperty isPauseProperty() {
        return isPause;
    }
    
    /**
     * Gets the game over property.
     */
    public BooleanProperty isGameOverProperty() {
        return isGameOver;
    }
    
    /**
     * Checks if the game is currently paused.
     */
    public boolean isPaused() {
        return isPause.getValue();
    }
    
    /**
     * Sets the pause state.
     */
    public void setPaused(boolean paused) {
        isPause.setValue(paused);
    }
    
    /**
     * Checks if the game is over.
     */
    public boolean isGameOver() {
        return isGameOver.getValue();
    }
    
    /**
     * Sets the game over state.
     */
    public void setGameOver(boolean gameOver) {
        isGameOver.setValue(gameOver);
    }
    
    /**
     * Checks if the game is currently active (not paused and not game over).
     */
    public boolean isActive() {
        return !isPaused() && !isGameOver();
    }
    
    /**
     * Resets all game states to their initial values.
     */
    public void reset() {
        isPause.setValue(false);
        isGameOver.setValue(false);
    }
}
