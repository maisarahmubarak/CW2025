package com.comp2042.ui.overlay;

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

/**
 * GameOverlayController coordinates pause and game over overlays.
 * Responsibilities:
 * - Coordinating PauseOverlayController and GameOverOverlayController
 * - Delegating visibility operations to specialized overlay controllers
 * - Managing callbacks for overlay actions
 * 
 * This is a thin coordinator that delegates to specialized overlay controllers,
 * keeping overlay management logic modular and focused.
 */
public class GameOverlayController {
    
    private final PauseOverlayController pauseOverlayController;
    private final GameOverOverlayController gameOverOverlayController;
    
    /**
     * Constructor for GameOverlayController.
     * 
     * @param gamePanel The game panel for layout
     * @param groupNotification The notification overlay pane
     * @param gameOverPanel The legacy game over panel
     * @param rootPane The root pane for overlay positioning
     */
    public GameOverlayController(GridPane gamePanel, 
                                 AnchorPane groupNotification,
                                 GameOverPanel gameOverPanel, 
                                 javafx.scene.layout.Pane rootPane) {
        this.pauseOverlayController = new PauseOverlayController(rootPane, groupNotification);
        this.gameOverOverlayController = new GameOverOverlayController(rootPane, groupNotification, gameOverPanel);
    }
    
    /**
     * Sets the toggle pause callback.
     * 
     * @param callback The callback to execute when pause is toggled
     */
    public void setOnTogglePause(Runnable callback) {
        pauseOverlayController.setOnTogglePause(callback);
    }
    
    /**
     * Sets the new game callback.
     * 
     * @param callback The callback to execute when starting a new game
     */
    public void setOnNewGame(Runnable callback) {
        pauseOverlayController.setOnNewGame(callback);
        gameOverOverlayController.setOnNewGame(callback);
    }
    
    /**
     * Sets the return to main menu callback.
     * 
     * @param callback The callback to execute when returning to main menu
     */
    public void setOnReturnToMainMenu(Runnable callback) {
        pauseOverlayController.setOnReturnToMainMenu(callback);
        gameOverOverlayController.setOnReturnToMainMenu(callback);
    }
    
    /**
     * Initializes all pause and game over overlays.
     */
    public void initializeOverlays() {
        pauseOverlayController.initialize();
        gameOverOverlayController.initialize();
    }
    
    /**
     * Shows the pause overlay.
     */
    public void showPauseOverlay() {
        pauseOverlayController.show();
    }

    /**
     * Hides the pause overlay.
     */
    public void hidePauseOverlay() {
        pauseOverlayController.hide();
    }
    
    /**
     * Shows the game over overlay.
     */
    public void showGameOverOverlay() {
        gameOverOverlayController.show();
    }
    
    /**
     * Hides the game over overlay.
     */
    public void hideGameOverOverlay() {
        gameOverOverlayController.hide();
    }
    
    /**
     * Sets the final score on the game over overlay.
     * 
     * @param score The final score to display
     */
    public void setFinalScore(int score) {
        gameOverOverlayController.setFinalScore(score);
    }
}
