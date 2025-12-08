package com.comp2042.ui.state;

import com.comp2042.input.InputActionListener;
import com.comp2042.logic.game.GameLoop;
import com.comp2042.logic.game.GameMode;
import com.comp2042.logic.game.GameSettings;
import com.comp2042.logic.game.ViewData;
import com.comp2042.ui.GameBoardView;
import com.comp2042.ui.overlay.GameOverPanel;
import com.comp2042.ui.overlay.GameOverlayController;
import javafx.beans.property.BooleanProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.AudioClip;
import javafx.util.Duration;

/**
 * GameLifecycleController coordinates the core game lifecycle:
 * - Starting a game (initGameView)
 * - Handling game over state
 * - Pausing and resuming
 * - Switching game modes
 * - Coordinating with specialized controllers for state management, pause logic, and danger mode
 * 
 * Delegates responsibilities to:
 * - GameStateManager: State properties (pause, game over)
 * - GamePauseController: Pause/resume and countdown logic
 * - DangerModeController: Danger mode effects (flash, boost, control flip)
 * - GameTimerController: Timer display management
 * - GameOverlayController: Overlay UI management
 */
public class GameLifecycleController {
    
    private final GridPane gamePanel;
    private final AnchorPane groupNotification;
    private final StackPane boardStack;
    
    private InputActionListener eventListener;
    private GameBoardView gameBoardView;
    private GameLoop gameLoop;
    
    // Delegated controllers
    private final GameStateManager stateManager;
    private final GamePauseController pauseController;
    private final DangerModeController dangerModeController;
    private final GameTimerController gameTimerController;
    private final GameOverlayController gameOverlayController;
    
    private AudioClip gameOverSound;
    
    // Callback for moveDown
    private Runnable moveDownCallback;
    
    /**
     * Constructor for GameLifecycleController.
     */
    public GameLifecycleController(GridPane gamePanel, AnchorPane groupNotification, 
                                   GameOverPanel gameOverPanel, javafx.scene.layout.Pane rootPane,
                                   StackPane boardStack, Label timerLabel) {
        this.gamePanel = gamePanel;
        this.groupNotification = groupNotification;
        this.boardStack = boardStack;
        
        // Initialize delegated controllers
        this.stateManager = new GameStateManager();
        this.pauseController = new GamePauseController(gamePanel, groupNotification, stateManager);
        this.dangerModeController = new DangerModeController(gamePanel, groupNotification, boardStack, stateManager);
        this.gameTimerController = new GameTimerController(timerLabel);
        this.gameOverlayController = new GameOverlayController(gamePanel, groupNotification, gameOverPanel, rootPane);
        
        // Wire pause controller callbacks
        pauseController.setOnResumeComplete(() -> {
            gameLoop.play();
            startTimer();
        });
        
        // Wire overlay callbacks
        gameOverlayController.setOnTogglePause(() -> togglePause());
        gameOverlayController.setOnNewGame(() -> newGame(null));
    }
    
    /**
     * Sets the game board view.
     */
    public void setGameBoardView(GameBoardView gameBoardView) {
        this.gameBoardView = gameBoardView;
    }
    
    /**
     * Sets the game loop.
     */
    public void setGameLoop(GameLoop gameLoop) {
        this.gameLoop = gameLoop;
        dangerModeController.setGameLoop(gameLoop);
    }
    
    /**
     * Sets the game over sound.
     */
    public void setGameOverSound(AudioClip gameOverSound) {
        this.gameOverSound = gameOverSound;
    }
    
    /**
     * Sets the move down callback.
     */
    public void setMoveDownCallback(Runnable callback) {
        this.moveDownCallback = callback;
    }
    
    /**
     * Gets the state manager for external access.
     */
    public GameStateManager getStateManager() {
        return stateManager;
    }
    
    /**
     * Gets the danger mode controller for external access.
     */
    public DangerModeController getDangerModeController() {
        return dangerModeController;
    }
    
    /**
     * Initializes the pause and game over overlays by delegating to GameOverlayController.
     */
    public void initializeOverlays() {
        gameOverlayController.initializeOverlays();
    }
    
    /**
     * Initializes the timer by delegating to GameTimerController.
     */
    public void initializeTimer() {
        gameTimerController.initializeTimer();
    }
    
    private void startTimer() {
        gameTimerController.startTimer();
    }
    
    private void stopTimer() {
        gameTimerController.stopTimer();
    }
    
    private void resetTimer() {
        gameTimerController.resetTimer();
    }
    
    /**
     * Initializes the game view with the given board matrix and brick data.
     */
    public void initGameView(int[][] boardMatrix, ViewData brick) {
        gameBoardView.initGameView(boardMatrix, brick);
        gameLoop.play();
        resetTimer();
        startTimer();
    }
    
    /**
     * Starts a new game.
     */
    public void newGame(ActionEvent actionEvent) {
        gameLoop.stop();
        stopTimer();
        pauseController.cancelResumeCountdown();
        dangerModeController.stopDangerMode();
        gameOverlayController.hideGameOverOverlay();
        eventListener.createNewGame();
        gamePanel.requestFocus();
        gameLoop.play();
        resetTimer();
        startTimer();
        stateManager.reset();
        gameOverlayController.hidePauseOverlay();
    }
    
    /**
     * Handles game over state.
     */
    public void gameOver() {
        gameLoop.stop();
        stopTimer();
        pauseController.cancelResumeCountdown();
        dangerModeController.stopDangerMode();
        gameOverlayController.hidePauseOverlay();
        
        // Play game over sound
        if (gameOverSound != null) {
            gameOverSound.setVolume(GameSettings.getVolume() / 100.0);
            gameOverSound.play();
        }

        gameOverlayController.showGameOverOverlay();
        stateManager.setGameOver(true);
    }
    
    /**
     * Sets the final score for the game over screen.
     */
    public void setFinalScore(int score) {
        gameOverlayController.setFinalScore(score);
    }
    
    /**
     * Pauses the game.
     */
    public void pauseGame(ActionEvent actionEvent) {
        togglePause();
    }
    
    /**
     * Toggles pause state.
     */
    public void togglePause() {
        if (stateManager.isGameOver()) {
            return;
        }
        if (!stateManager.isPaused()) {
            stateManager.setPaused(true);
            gameLoop.stop();
            stopTimer();
            pauseController.cancelResumeCountdown();
            gameOverlayController.showPauseOverlay();
        } else {
            if (pauseController.isCountdownActive()) {
                pauseController.cancelResumeCountdown();
            } else {
                pauseController.beginResumeCountdown();
            }
            gameOverlayController.hidePauseOverlay();
        }
        gamePanel.requestFocus();
    }
    
    /**
     * Sets the game mode.
     */
    public void setGameMode(GameMode mode) {
        if (mode == null) {
            return;
        }
        if (gameLoop != null) {
            gameLoop.stop();
        }
        gameLoop = new GameLoop(Duration.millis(mode.getDropIntervalMs()), () -> {
            if (moveDownCallback != null) {
                moveDownCallback.run();
            }
        });
        
        // Update danger mode controller with new game loop and interval
        dangerModeController.setGameLoop(gameLoop);
        dangerModeController.setBaseInterval(mode.getDropIntervalMs());
        
        if (stateManager.isActive()) {
            gameLoop.play();
        }
        
        // Start or stop danger mode effects
        if (mode == GameMode.DANGER) {
            dangerModeController.startDangerMode();
        } else {
            dangerModeController.stopDangerMode();
        }
    }
    
    /**
     * Sets the input event listener.
     */
    public void setEventListener(InputActionListener eventListener) {
        this.eventListener = eventListener;
    }
    
    /**
     * Sets the callback for returning to the main menu.
     */
    public void setOnReturnToMainMenu(Runnable r) {
        if (gameOverlayController != null) {
            gameOverlayController.setOnReturnToMainMenu(r);
        }
    }
    
    // Getter methods for state (delegate to state manager and controllers)
    
    public BooleanProperty isPauseProperty() {
        return stateManager.isPauseProperty();
    }
    
    public BooleanProperty isGameOverProperty() {
        return stateManager.isGameOverProperty();
    }
    
    public boolean isControlsFlipped() {
        return dangerModeController.isControlsFlipped();
    }
    
    public GameLoop getGameLoop() {
        return gameLoop;
    }
}
