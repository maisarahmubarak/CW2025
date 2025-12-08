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
     * Sets the game loop.
     */
    public void setGameLoop(GameLoop gameLoop) {
        this.gameLoop = gameLoop;
    }
    
    /**
     * Binds the score display to the game score.
     */
    public void bindToScore(GameScore score) {
        if (scoreUiController != null) {
            scoreUiController.bindToScore(score);
        }
    }
    
    /**
     * Handles keyboard input events by delegating to GameInputController.
     */
    public void handleKeyEvent(KeyEvent keyEvent) {
        if (gameInputController != null) {
            gameInputController.handleKeyEvent(keyEvent);
        }
    }
    
    /**
     * Initializes the game view with the given board matrix and brick data.
     */
    public void initGameView(int[][] boardMatrix, ViewData brick) {
        gameLifecycleController.initGameView(boardMatrix, brick);
    }
    
    /**
     * Refreshes the game background with the current board state.
     */
    public void refreshGameBackground(int[][] board) {
        gameBoardView.refreshGameBackground(board);
    }
    
    /**
     * Sets the game speed by updating the game loop interval.
     */
    public void setGameSpeed(Duration duration) {
        if (gameLifecycleController != null && gameLifecycleController.getGameLoop() != null) {
            gameLifecycleController.getGameLoop().updateInterval(duration);
        }
    }
    
    /**
     * Sets the color palette for brick rendering.
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
     * Handles the moveDown action during gameplay.
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
     * Sets the final score for the game over screen.
     */
    public void setFinalScore(int score) {
        gameLifecycleController.setFinalScore(score);
    }
    
    /**
     * Triggers the game over state.
     */
    public void gameOver() {
        gameLifecycleController.gameOver();
    }
    
    /**
     * Animates cleared rows by delegating to BoardAnimationController.
     */
    public void animateClearedRows(int[][] prevMatrix, ClearRow clearRow) {
        if (boardAnimationController != null) {
            boardAnimationController.animateClearedRows(prevMatrix, clearRow);
        }
    }
    
    /**
     * Sets the game mode and updates the game loop accordingly.
     */
    public void setGameMode(GameMode mode) {
        gameLifecycleController.setGameMode(mode);
        // Update local reference to gameLoop
        gameLoop = gameLifecycleController.getGameLoop();
    }
}
