package com.comp2042.ui;

import com.comp2042.BrickColorPalette;
import com.comp2042.ClassicBrickPalette;
import com.comp2042.ui.effects.BoardAnimationController;
import com.comp2042.ui.effects.NeonGridBackground;
import com.comp2042.ui.effects.RetroParticleBackground;
import com.comp2042.ui.overlay.GameOverPanel;
import com.comp2042.ui.overlay.NotificationPanel;
import com.comp2042.ui.effects.TitleRevealAnimator;
import com.comp2042.input.ActionSource;
import com.comp2042.input.ActionType;
import com.comp2042.input.GameKeyHandler;
import com.comp2042.input.InputActionListener;
import com.comp2042.input.MoveAction;
import com.comp2042.logic.board.ClearRow;
import com.comp2042.logic.game.DownData;
import com.comp2042.logic.game.GameLoop;
import com.comp2042.logic.game.GameMode;
import com.comp2042.logic.game.GameScore;
import com.comp2042.logic.game.GameSettings;
import com.comp2042.logic.game.ViewData;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.PauseTransition;
import javafx.animation.FadeTransition;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import java.util.Random;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.util.Duration;
import javafx.animation.TranslateTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ParallelTransition;
import javafx.scene.media.AudioClip;
import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;

import java.net.URL;
import java.util.ResourceBundle;

public class GuiController implements Initializable {

    private static final int BRICK_SIZE = 20;

    @FXML
    private GridPane gamePanel;
    @FXML
    private StackPane boardStack;

    @FXML
    private AnchorPane groupNotification;

    @FXML
    private GridPane brickPanel;

    @FXML
    private GridPane previewPanel;

    @FXML
    private GameOverPanel gameOverPanel;
    @FXML
    private BorderPane gameBoard;
    @FXML
    private javafx.scene.layout.Pane rootPane;
    @FXML
    private AnchorPane contentPane;
    
    private NeonGridBackground neonGridBackground;
    
    @FXML
    private VBox scorePanel;

    @FXML
    private Label scoreLabel;

    @FXML
    private Label highScoreLabel;

    @FXML
    private VBox timerPanel;

    @FXML
    private Label timerLabel;

    private Timeline timerTimeline;
    private int elapsedSeconds = 0;

    private GameStateController gameStateController;
    private GameInputController gameInputController;
    private ScoreUiController scoreUiController;
    private BoardAnimationController boardAnimationController;
    private InputActionListener eventListener;
    private GameBoardView gameBoardView;
    private GameLoop gameLoop;
    private BrickColorPalette palette = new ClassicBrickPalette();

    private AudioClip gameOverSound;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize ScoreUiController
        scoreUiController = new ScoreUiController(scoreLabel, highScoreLabel);
        scoreUiController.initialize();
        
        Font.loadFont(getClass().getClassLoader().getResource("digital.ttf").toExternalForm(), 38);
        
        // Load sounds
        try {
            URL rowClearUrl = getClass().getClassLoader().getResource("Tetris_RowClear.wav");
            if (rowClearUrl == null) rowClearUrl = getClass().getClassLoader().getResource("sounds/Tetris_RowClear.wav");
            AudioClip rowClearSound = null;
            if (rowClearUrl != null) rowClearSound = new AudioClip(rowClearUrl.toExternalForm());

            URL gameOverUrl = getClass().getClassLoader().getResource("Tetris_GameOver.wav");
            if (gameOverUrl == null) gameOverUrl = getClass().getClassLoader().getResource("sounds/Tetris_GameOver.wav");
            if (gameOverUrl != null) gameOverSound = new AudioClip(gameOverUrl.toExternalForm());
            
            // Initialize BoardAnimationController with row clear sound
            boardAnimationController = new BoardAnimationController(boardStack, groupNotification, gameBoardView, palette);
            if (rowClearSound != null) {
                boardAnimationController.setRowClearSound(rowClearSound);
            }
        } catch (Exception e) {
            System.err.println("Could not load sounds: " + e.getMessage());
        }

        // Initialize the 80s neon grid animated background
        initNeonGridBackground();
        
        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();
        gamePanel.setOnKeyPressed(new GameKeyHandler(this));
        gameBoardView = new GameBoardView(gamePanel, brickPanel, previewPanel, BRICK_SIZE);
        gameBoardView.setPalette(palette);
        brickPanel.toFront();
        gameLoop = new GameLoop(Duration.millis(400), () -> moveDown(new MoveAction(ActionType.DOWN, ActionSource.THREAD)));
        gameOverPanel.setVisible(false);
        
        // Initialize GameStateController
        gameStateController = new GameStateController(gamePanel, groupNotification, gameOverPanel, rootPane, boardStack, timerLabel);
        gameStateController.setGameBoardView(gameBoardView);
        gameStateController.setGameLoop(gameLoop);
        gameStateController.setGameOverSound(gameOverSound);
        gameStateController.setMoveDownCallback(() -> moveDown(new MoveAction(ActionType.DOWN, ActionSource.THREAD)));
        gameStateController.initializeOverlays();
        gameStateController.initializeTimer();
        
        // Initialize GameInputController
        gameInputController = new GameInputController(
            gameStateController,
            gameBoardView,
            eventListener,
            () -> moveDown(new MoveAction(ActionType.DOWN, ActionSource.USER))
        );
        
        // Ensure that the overlay anchor pane fills the whole window, so overlays are fullscreen
        if (groupNotification != null && rootPane != null) {
            groupNotification.prefWidthProperty().bind(rootPane.widthProperty());
            groupNotification.prefHeightProperty().bind(rootPane.heightProperty());
        }
        applyBrightness();
    }

    private void applyBrightness() {
        if (rootPane != null) {
             double sliderVal = GameSettings.getBrightness();
             double colorAdjustVal = sliderVal - 1.0; 
             javafx.scene.effect.ColorAdjust adjust = new javafx.scene.effect.ColorAdjust();
             adjust.setBrightness(colorAdjustVal);
             rootPane.setEffect(adjust);
        }
    }

    /**
     * Initializes the animated 80s-style neon grid background.
     * The background is added behind all other UI elements and starts animating.
     */
    private void initNeonGridBackground() {
        if (rootPane == null) {
            return;
        }
        
        neonGridBackground = new NeonGridBackground();
        
        // Bind the background size to the root pane
        neonGridBackground.prefWidthProperty().bind(rootPane.widthProperty());
        neonGridBackground.prefHeightProperty().bind(rootPane.heightProperty());
        
        // Insert the background at position 0 so it's behind everything else
        if (rootPane instanceof StackPane) {
            ((StackPane) rootPane).getChildren().add(0, neonGridBackground);
        }
        
        // Start the animation
        neonGridBackground.start();
    }



    /**
     * Binds the score display to the game score by delegating to ScoreUiController.
     * 
     * @param score The GameScore object to bind to
     */
    public void bindToScore(GameScore score) {
        if (scoreUiController != null) {
            scoreUiController.bindToScore(score);
        }
    }

    // Moved the anonymous key handler here so key-handling logic lives in one place
    // and can be delegated to from a separate GameKeyHandler class.
    /**
     * Handles keyboard input events by delegating to GameInputController.
     * All key mappings and input logic are now managed by GameInputController.
     */
    public void handleKeyEvent(KeyEvent keyEvent) {
        if (gameInputController != null) {
            gameInputController.handleKeyEvent(keyEvent);
        }
    }

    public void initGameView(int[][] boardMatrix, ViewData brick) {
        gameStateController.initGameView(boardMatrix, brick);
    }

    public void refreshGameBackground(int[][] board) {
        gameBoardView.refreshGameBackground(board);
    }

    public void setGameSpeed(Duration duration) {
        if (gameStateController != null && gameStateController.getGameLoop() != null) {
            gameStateController.getGameLoop().updateInterval(duration);
        }
    }

    public void setColorPalette(BrickColorPalette palette) {
        if (palette == null) {
            return;
        }
        this.palette = palette;
        if (gameBoardView != null) {
            gameBoardView.setPalette(palette);
        }
    }

    private void moveDown(MoveAction event) {
        if (gameStateController.isPauseProperty().getValue() == Boolean.FALSE) {
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

    public void setEventListener(InputActionListener eventListener) {
        this.eventListener = eventListener;
        if (gameStateController != null) {
            gameStateController.setEventListener(eventListener);
        }
        if (gameInputController != null) {
            gameInputController.setEventListener(eventListener);
        }
    }

    public void setFinalScore(int score) {
        gameStateController.setFinalScore(score);
    }



    public void gameOver() {
        gameStateController.gameOver();
    }

    /**
     * Animates cleared rows by delegating to BoardAnimationController.
     * Visual-only animation: turn cleared row blocks into falling and vanishing rectangles.
     * This does not alter game state; it only animates an overlay based on the previous board snapshot.
     * 
     * @param prevMatrix The board matrix before rows were cleared
     * @param clearRow Information about which rows were cleared
     */
    public void animateClearedRows(int[][] prevMatrix, ClearRow clearRow) {
        if (boardAnimationController != null) {
            boardAnimationController.animateClearedRows(prevMatrix, clearRow);
        }
    }

    public void setOnReturnToMainMenu(Runnable r) {
        gameStateController.setOnReturnToMainMenu(r);
    }

    public void newGame(ActionEvent actionEvent) {
        gameStateController.newGame(actionEvent);
    }

    public void pauseGame(ActionEvent actionEvent) {
        gameStateController.pauseGame(actionEvent);
    }



    /**
     * Allows an external caller to set the current game mode (for example, for speed adjustments).
     * Recreates the underlying game loop with the new interval.
     */
    public void setGameMode(GameMode mode) {
        gameStateController.setGameMode(mode);
        // Update local reference to gameLoop
        gameLoop = gameStateController.getGameLoop();
    }




}
