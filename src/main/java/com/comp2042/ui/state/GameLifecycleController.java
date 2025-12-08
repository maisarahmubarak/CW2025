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
    /**
     * Constructs a new GameLifecycleController.
     * <p>
     * Initializes the controller and its delegated sub-controllers (state, pause, danger, timer, overlay).
     * Sets up callbacks for pause/resume and overlay interactions.
     * </p>
     * 
     * @param gamePanel The main game grid pane.
     * @param groupNotification The overlay notification pane.
     * @param gameOverPanel The game over panel component.
     * @param rootPane The root pane of the scene.
     * @param boardStack The stack pane containing the board.
     * @param timerLabel The label for displaying the game timer.
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
     * <p>
     * Updates the reference to the view component used for rendering the game.
     * </p>
     *
     * @param gameBoardView The GameBoardView to set.
     */
    public void setGameBoardView(GameBoardView gameBoardView) {
        this.gameBoardView = gameBoardView;
    }
    
    /**
     * Sets the game loop.
     * <p>
     * Updates the game loop reference and propagates it to the DangerModeController.
     * </p>
     *
     * @param gameLoop The GameLoop to set.
     */
    public void setGameLoop(GameLoop gameLoop) {
        this.gameLoop = gameLoop;
        dangerModeController.setGameLoop(gameLoop);
    }
    
    /**
     * Sets the game over sound effect.
     * <p>
     * Updates the audio clip played when the game ends.
     * </p>
     *
     * @param gameOverSound The AudioClip to set.
     */
    public void setGameOverSound(AudioClip gameOverSound) {
        this.gameOverSound = gameOverSound;
    }
    
    /**
     * Sets the callback for the move down action.
     * <p>
     * Updates the runnable executed when the game loop ticks or soft drop is triggered.
     * </p>
     *
     * @param callback The Runnable to set.
     */
    public void setMoveDownCallback(Runnable callback) {
        this.moveDownCallback = callback;
    }
    
    /**
     * Gets the state manager.
     * <p>
     * Provides access to the GameStateManager for checking game state properties.
     * </p>
     *
     * @return The GameStateManager instance.
     */
    public GameStateManager getStateManager() {
        return stateManager;
    }
    
    /**
     * Gets the danger mode controller.
     * <p>
     * Provides access to the DangerModeController for managing danger mode effects.
     * </p>
     *
     * @return The DangerModeController instance.
     */
    public DangerModeController getDangerModeController() {
        return dangerModeController;
    }
    
    /**
     * Initializes the game overlays.
     * <p>
     * Delegates to the GameOverlayController to set up the pause and game over screens.
     * </p>
     */
    public void initializeOverlays() {
        gameOverlayController.initializeOverlays();
    }
    
    /**
     * Initializes the game timer.
     * <p>
     * Delegates to the GameTimerController to reset and prepare the timer.
     * </p>
     */
    public void initializeTimer() {
        gameTimerController.initializeTimer();
    }
    
    /**
     * Starts the game timer.
     */
    private void startTimer() {
        gameTimerController.startTimer();
    }
    
    /**
     * Stops the game timer.
     */
    private void stopTimer() {
        gameTimerController.stopTimer();
    }
    
    /**
     * Resets the game timer to zero.
     */
    private void resetTimer() {
        gameTimerController.resetTimer();
    }
    
    /**
     * Initializes the game view and starts the game.
     * <p>
     * Sets up the board view with the initial state, starts the game loop, and starts the timer.
     * </p>
     *
     * @param boardMatrix The initial board matrix.
     * @param brick The initial active brick view data.
     */
    public void initGameView(int[][] boardMatrix, ViewData brick) {
        gameBoardView.initGameView(boardMatrix, brick);
        gameLoop.play();
        resetTimer();
        startTimer();
    }
    
    /**
     * Starts a new game session.
     * <p>
     * Resets the game state, stops current loops and timers, clears overlays,
     * triggers the new game event, and restarts the game loop and timer.
     * </p>
     *
     * @param actionEvent The event triggering the new game (can be null).
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
     * Handles the game over state.
     * <p>
     * Stops the game loop and timer, plays the game over sound, shows the game over overlay,
     * and updates the game state to "game over".
     * </p>
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
     * Sets the final score to be displayed on the game over screen.
     * <p>
     * Delegates to the GameOverlayController.
     * </p>
     *
     * @param score The final score achieved.
     */
    public void setFinalScore(int score) {
        gameOverlayController.setFinalScore(score);
    }
    
    /**
     * Pauses the game via an action event.
     * <p>
     * Delegates to {@link #togglePause()}.
     * </p>
     *
     * @param actionEvent The event triggering the pause.
     */
    public void pauseGame(ActionEvent actionEvent) {
        togglePause();
    }
    
    /**
     * Toggles the pause state of the game.
     * <p>
     * If the game is running, it pauses the game loop and timer and shows the pause overlay.
     * If the game is paused, it starts the resume countdown (or cancels it if already counting down).
     * Does nothing if the game is over.
     * </p>
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
     * Sets the game mode and configures the game loop.
     * <p>
     * Updates the game loop with the drop interval defined by the game mode.
     * Also handles starting/stopping Danger mode effects.
     * </p>
     *
     * @param mode The GameMode to set.
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
     * <p>
     * Updates the listener used to trigger game actions like creating a new game.
     * </p>
     *
     * @param eventListener The InputActionListener to set.
     */
    public void setEventListener(InputActionListener eventListener) {
        this.eventListener = eventListener;
    }
    
    /**
     * Sets the callback for returning to the main menu.
     * <p>
     * Delegates to the GameOverlayController.
     * </p>
     *
     * @param r The Runnable to execute.
     */
    public void setOnReturnToMainMenu(Runnable r) {
        if (gameOverlayController != null) {
            gameOverlayController.setOnReturnToMainMenu(r);
        }
    }
    
    // Getter methods for state (delegate to state manager and controllers)
    
    /**
     * Gets the pause property.
     * 
     * @return The BooleanProperty representing the pause state.
     */
    public BooleanProperty isPauseProperty() {
        return stateManager.isPauseProperty();
    }
    
    /**
     * Gets the game over property.
     * 
     * @return The BooleanProperty representing the game over state.
     */
    public BooleanProperty isGameOverProperty() {
        return stateManager.isGameOverProperty();
    }
    
    /**
     * Checks if controls are currently flipped (e.g., in Danger mode).
     * 
     * @return true if controls are flipped, false otherwise.
     */
    public boolean isControlsFlipped() {
        return dangerModeController.isControlsFlipped();
    }
    
    /**
     * Gets the current game loop.
     * 
     * @return The GameLoop instance.
     */
    public GameLoop getGameLoop() {
        return gameLoop;
    }
}
