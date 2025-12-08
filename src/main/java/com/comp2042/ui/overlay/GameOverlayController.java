package com.comp2042.ui.overlay;

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

/**
 * Coordinates the display and interaction of game overlays (Pause and Game Over).
 * <p>
 * This controller acts as a facade for managing the different overlay states in the game.
 * It delegates specific rendering and logic to {@link PauseOverlayController} and
 * {@link GameOverOverlayController}, ensuring a clean separation of concerns.
 * </p>
 * <p>
 * Responsibilities include:
 * <ul>
 *   <li>Initializing and managing overlay sub-controllers.</li>
 *   <li>Routing callbacks (e.g., resume, restart, quit) to the appropriate handlers.</li>
 *   <li>Controlling the visibility of pause and game-over screens.</li>
 * </ul>
 * </p>
 */
public class GameOverlayController {
    
    private final PauseOverlayController pauseOverlayController;
    private final GameOverOverlayController gameOverOverlayController;
    
    /**
     * Constructs a new GameOverlayController.
     * <p>
     * Initializes the specialized controllers for the pause menu and game over screen.
     * </p>
     * 
     * @param gamePanel         The main game grid panel (used for layout context).
     * @param groupNotification The anchor pane used for displaying notifications/overlays.
     * @param gameOverPanel     The custom component representing the game over UI.
     * @param rootPane          The root container of the scene, used for centering overlays.
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
