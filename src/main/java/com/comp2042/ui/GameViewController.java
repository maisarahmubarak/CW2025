package com.comp2042.ui;

import com.comp2042.BrickColorPalette;
import com.comp2042.ClassicBrickPalette;
import com.comp2042.input.ActionSource;
import com.comp2042.input.ActionType;
import com.comp2042.input.InputActionListener;
import com.comp2042.input.MoveAction;
import com.comp2042.logic.board.ClearRow;
import com.comp2042.logic.game.DownData;
import com.comp2042.logic.game.GameLoop;
import com.comp2042.logic.game.GameMode;
import com.comp2042.logic.game.GameScore;
import com.comp2042.logic.game.ViewData;
import com.comp2042.ui.effects.BoardAnimationController;
import com.comp2042.ui.overlay.NotificationPanel;
import com.comp2042.ui.state.GameLifecycleController;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

/**
 * GameViewController handles all game view operations and event management.
 * Responsibilities:
 * - Managing game board view updates
 * - Processing move actions and game events
 * - Coordinating animations and visual effects
 * - Delegating to specialized controllers (lifecycle, input, animation)
 */
public class GameViewController {
    
    private final GridPane gamePanel;
    private final AnchorPane groupNotification;
    
    private final GameLifecycleController gameLifecycleController;
    private final GameInputController gameInputController;
    private final BoardAnimationController boardAnimationController;
    private final ScoreUiController scoreUiController;
    
    private InputActionListener eventListener;
    private GameBoardView gameBoardView;
    private GameLoop gameLoop;
    private BrickColorPalette palette = new ClassicBrickPalette();
    
    /**
     * Constructor for GameViewController.
     * 
     * @param gamePanel The main game panel
     * @param groupNotification The notification overlay pane
     * @param gameLifecycleController The lifecycle controller
     * @param gameInputController The input controller
     * @param boardAnimationController The animation controller
     * @param scoreUiController The score UI controller
     * @param gameBoardView The game board view
     */
    /**
     * Constructs a new GameViewController.
     * <p>
     * Initializes the controller with all necessary dependencies for managing the game view.
     * </p>
     * 
     * @param gamePanel The main GridPane where the game board is rendered.
     * @param groupNotification The AnchorPane used for displaying overlay notifications.
     * @param gameLifecycleController The controller managing the game's lifecycle (start, pause, over).
     * @param gameInputController The controller handling user input.
     * @param boardAnimationController The controller managing board animations (e.g., row clear).
     * @param scoreUiController The controller managing the score display.
     * @param gameBoardView The view component responsible for rendering the board and bricks.
     */
    public GameViewController(GridPane gamePanel, AnchorPane groupNotification,
                             GameLifecycleController gameLifecycleController,
                             GameInputController gameInputController,
                             BoardAnimationController boardAnimationController,
                             ScoreUiController scoreUiController,
                             GameBoardView gameBoardView) {
        this.gamePanel = gamePanel;
        this.groupNotification = groupNotification;
        this.gameLifecycleController = gameLifecycleController;
        this.gameInputController = gameInputController;
        this.boardAnimationController = boardAnimationController;
        this.scoreUiController = scoreUiController;
        this.gameBoardView = gameBoardView;
    }
    
    /**
     * Sets the game loop instance.
     * <p>
     * Allows updating the game loop reference, which is used for controlling game speed.
     * </p>
     *
     * @param gameLoop The GameLoop instance to set.
     */
    public void setGameLoop(GameLoop gameLoop) {
        this.gameLoop = gameLoop;
    }
    
    /**
     * Binds the score display to the game score property.
     * <p>
     * Delegates to the ScoreUiController to ensure the UI updates when the score changes.
     * </p>
     *
     * @param score The GameScore object to bind to.
     */
    public void bindToScore(GameScore score) {
        if (scoreUiController != null) {
            scoreUiController.bindToScore(score);
        }
    }
    
    /**
     * Handles keyboard input events.
     * <p>
     * Delegates the processing of key events to the GameInputController.
     * </p>
     *
     * @param keyEvent The KeyEvent to handle.
     */
    public void handleKeyEvent(KeyEvent keyEvent) {
        if (gameInputController != null) {
            gameInputController.handleKeyEvent(keyEvent);
        }
    }
    
    /**
     * Initializes the game view with the initial board state and active brick.
     * <p>
     * Delegates to the GameLifecycleController to set up the initial view state.
     * </p>
     *
     * @param boardMatrix The initial board matrix.
     * @param brick The initial active brick view data.
     */
    public void initGameView(int[][] boardMatrix, ViewData brick) {
        gameLifecycleController.initGameView(boardMatrix, brick);
    }
    
    /**
     * Refreshes the game background grid.
     * <p>
     * Updates the visual representation of the board based on the provided matrix.
     * </p>
     *
     * @param board The current board matrix.
     */
    public void refreshGameBackground(int[][] board) {
        gameBoardView.refreshGameBackground(board);
    }
    
    /**
     * Sets the game speed by updating the game loop interval.
     * <p>
     * Adjusts the drop interval of the game loop to control the speed of the falling bricks.
     * </p>
     *
     * @param duration The new duration for the drop interval.
     */
    public void setGameSpeed(Duration duration) {
        if (gameLifecycleController != null && gameLifecycleController.getGameLoop() != null) {
            gameLifecycleController.getGameLoop().updateInterval(duration);
        }
    }
    
    /**
     * Sets the color palette for the bricks.
     * <p>
     * Updates the palette used by the GameBoardView for rendering bricks.
     * </p>
     *
     * @param palette The BrickColorPalette to use.
     */
    public void setColorPalette(BrickColorPalette palette) {
        if (palette == null) {
            return;
        }
        this.palette = palette;
        if (gameBoardView != null) {
            gameBoardView.setPalette(palette);
        }
    }
    
    /**
     * Handles the "move down" action.
     * <p>
     * Processes the downward movement of the brick. If the game is not paused, it triggers the
     * onDownEvent on the listener. If rows are cleared, it shows a notification and updates the view.
     * </p>
     *
     * @param event The MoveAction event details.
     */
    public void moveDown(MoveAction event) {
        if (gameLifecycleController.isPauseProperty().getValue() == Boolean.FALSE) {
            DownData downData = eventListener.onDownEvent(event);
            if (downData.getClearRow() != null && downData.getClearRow().getLinesRemoved() > 0) {
                NotificationPanel notificationPanel = new NotificationPanel("+" + downData.getClearRow().getScoreBonus());
                groupNotification.getChildren().add(notificationPanel);
                notificationPanel.showScore(groupNotification.getChildren());
            }
            gameBoardView.refreshBrick(downData.getViewData());
        }
        gamePanel.requestFocus();
    }
    
    /**
     * Sets the event listener for game actions.
     * <p>
     * Registers the listener with this controller and propagates it to the lifecycle and input controllers.
     * </p>
     *
     * @param eventListener The InputActionListener to receive game events.
     */
    public void setEventListener(InputActionListener eventListener) {
        this.eventListener = eventListener;
        if (gameLifecycleController != null) {
            gameLifecycleController.setEventListener(eventListener);
        }
        if (gameInputController != null) {
            gameInputController.setEventListener(eventListener);
        }
    }
    
    /**
     * Sets the final score to be displayed on the game over screen.
     * <p>
     * Delegates to the GameLifecycleController.
     * </p>
     *
     * @param score The final score achieved.
     */
    public void setFinalScore(int score) {
        gameLifecycleController.setFinalScore(score);
    }
    
    /**
     * Triggers the game over sequence.
     * <p>
     * Delegates to the GameLifecycleController to handle the game over state.
     * </p>
     */
    public void gameOver() {
        gameLifecycleController.gameOver();
    }
    
    /**
     * Animates the clearing of rows.
     * <p>
     * Delegates to the BoardAnimationController to perform the visual effects.
     * </p>
     *
     * @param prevMatrix The board matrix before the rows were cleared.
     * @param clearRow The object containing details about the cleared rows.
     */
    public void animateClearedRows(int[][] prevMatrix, ClearRow clearRow) {
        if (boardAnimationController != null) {
            boardAnimationController.animateClearedRows(prevMatrix, clearRow);
        }
    }
    
    /**
     * Sets the game mode for the current session.
     * <p>
     * Updates the game mode in the lifecycle controller and refreshes the local game loop reference.
     * </p>
     *
     * @param mode The GameMode to set.
     */
    public void setGameMode(GameMode mode) {
        gameLifecycleController.setGameMode(mode);
        // Update local reference to gameLoop
        gameLoop = gameLifecycleController.getGameLoop();
    }
}
