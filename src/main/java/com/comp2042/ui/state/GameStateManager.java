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
     * <p>
     * This property indicates whether the game is currently paused.
     * </p>
     *
     * @return The BooleanProperty representing the pause state.
     */
    public BooleanProperty isPauseProperty() {
        return isPause;
    }
    
    /**
     * Gets the game over property.
     * <p>
     * This property indicates whether the game has ended.
     * </p>
     *
     * @return The BooleanProperty representing the game over state.
     */
    public BooleanProperty isGameOverProperty() {
        return isGameOver;
    }
    
    /**
     * Checks if the game is currently paused.
     * 
     * @return true if the game is paused, false otherwise.
     */
    public boolean isPaused() {
        return isPause.getValue();
    }
    
    /**
     * Sets the pause state of the game.
     * 
     * @param paused true to pause the game, false to resume.
     */
    public void setPaused(boolean paused) {
        isPause.setValue(paused);
    }
    
    /**
     * Checks if the game is over.
     * 
     * @return true if the game is over, false otherwise.
     */
    public boolean isGameOver() {
        return isGameOver.getValue();
    }
    
    /**
     * Sets the game over state.
     * 
     * @param gameOver true if the game is over, false otherwise.
     */
    public void setGameOver(boolean gameOver) {
        isGameOver.setValue(gameOver);
    }
    
    /**
     * Checks if the game is currently active.
     * <p>
     * A game is considered active if it is neither paused nor over.
     * </p>
     * 
     * @return true if the game is active, false otherwise.
     */
    public boolean isActive() {
        return !isPaused() && !isGameOver();
    }
    
    /**
     * Resets all game states to their initial values.
     * <p>
     * Sets pause and game over states to false.
     * </p>
     */
    public void reset() {
        isPause.setValue(false);
        isGameOver.setValue(false);
    }
    
    /**
     * Updates the game state logic.
     * <p>
     * This method is called on every frame of the game loop.
     * Currently, it serves as a placeholder for any state-dependent updates.
     * </p>
     */
    public void update() {
        // Placeholder for future state update logic if needed
    }
}
