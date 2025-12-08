package com.comp2042.ui;

import com.comp2042.BrickColorPalette;
import com.comp2042.ClassicBrickPalette;
import com.comp2042.ui.effects.BackgroundEffectController;
import com.comp2042.ui.effects.BoardAnimationController;
import com.comp2042.ui.overlay.GameOverPanel;
import com.comp2042.ui.state.GameLifecycleController;
import com.comp2042.input.ActionSource;
import com.comp2042.input.ActionType;
import com.comp2042.input.GameKeyHandler;
import com.comp2042.input.InputActionListener;
import com.comp2042.input.MoveAction;
import com.comp2042.logic.board.ClearRow;
import com.comp2042.logic.game.GameLoop;
import com.comp2042.logic.game.GameMode;
import com.comp2042.logic.game.GameScore;
import com.comp2042.logic.game.GameSettings;
import com.comp2042.logic.game.ViewData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.util.Duration;
import javafx.scene.media.AudioClip;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * GuiController manages FXML bindings and initializes game components.
 * Responsibilities:
 * - FXML component binding and initialization
 * - Creating and wiring controllers (lifecycle, input, animation, etc.)
 * - Loading resources (fonts, sounds)
 * - Delegating game operations to GameViewController
 */
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

    private GameLifecycleController gameLifecycleController;
    private GameViewController gameViewController;
    private BackgroundEffectController backgroundEffectController;
    private BrickColorPalette palette = new ClassicBrickPalette();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Load font
        Font.loadFont(getClass().getClassLoader().getResource("digital.ttf").toExternalForm(), 38);
        
        // Setup input handling
        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();
        gamePanel.setOnKeyPressed(new GameKeyHandler(this));
        
        // Create gameBoardView
        GameBoardView gameBoardView = new GameBoardView(gamePanel, brickPanel, previewPanel, BRICK_SIZE);
        gameBoardView.setPalette(palette);
        
        // Initialize ScoreUiController
        ScoreUiController scoreUiController = new ScoreUiController(scoreLabel, highScoreLabel);
        scoreUiController.initialize();
        
        // Load sounds
        AudioClip gameOverSound = null;
        AudioClip rowClearSound = null;
        try {
            URL rowClearUrl = getClass().getClassLoader().getResource("Tetris_RowClear.wav");
            if (rowClearUrl == null) rowClearUrl = getClass().getClassLoader().getResource("sounds/Tetris_RowClear.wav");
            if (rowClearUrl != null) rowClearSound = new AudioClip(rowClearUrl.toExternalForm());

            URL gameOverUrl = getClass().getClassLoader().getResource("Tetris_GameOver.wav");
            if (gameOverUrl == null) gameOverUrl = getClass().getClassLoader().getResource("sounds/Tetris_GameOver.wav");
            if (gameOverUrl != null) gameOverSound = new AudioClip(gameOverUrl.toExternalForm());
        } catch (Exception e) {
            System.err.println("Could not load sounds: " + e.getMessage());
        }
        
        // Initialize BoardAnimationController
        BoardAnimationController boardAnimationController = new BoardAnimationController(boardStack, groupNotification, gameBoardView, palette);
        if (rowClearSound != null) {
            boardAnimationController.setRowClearSound(rowClearSound);
        }

        // Initialize BackgroundEffectController
        backgroundEffectController = new BackgroundEffectController(rootPane);
        backgroundEffectController.initialize();
        brickPanel.toFront();
        
        // Initialize game loop
        GameLoop gameLoop = new GameLoop(Duration.millis(400), () -> {
            if (gameViewController != null) {
                gameViewController.moveDown(new MoveAction(ActionType.DOWN, ActionSource.THREAD));
            }
        });
        gameOverPanel.setVisible(false);
        
        // Initialize GameLifecycleController
        gameLifecycleController = new GameLifecycleController(gamePanel, groupNotification, gameOverPanel, rootPane, boardStack, timerLabel);
        gameLifecycleController.setGameBoardView(gameBoardView);
        gameLifecycleController.setGameLoop(gameLoop);
        gameLifecycleController.setGameOverSound(gameOverSound);
        gameLifecycleController.setMoveDownCallback(() -> {
            if (gameViewController != null) {
                gameViewController.moveDown(new MoveAction(ActionType.DOWN, ActionSource.THREAD));
            }
        });
        gameLifecycleController.initializeOverlays();
        gameLifecycleController.initializeTimer();
        
        // Initialize GameInputController
        GameInputController gameInputController = new GameInputController(
            gameLifecycleController,
            gameBoardView,
            null, // eventListener will be set later
            () -> {
                if (gameViewController != null) {
                    gameViewController.moveDown(new MoveAction(ActionType.DOWN, ActionSource.USER));
                }
            }
        );
        
        // Initialize GameViewController
        gameViewController = new GameViewController(
            gamePanel,
            groupNotification,
            gameLifecycleController,
            gameInputController,
            boardAnimationController,
            scoreUiController,
            gameBoardView
        );
        gameViewController.setGameLoop(gameLoop);
        
        // Ensure that the overlay anchor pane fills the whole window
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
    
    // Delegation methods to GameViewController
    
    /**
     * Binds the score display to the game score.
     */
    public void bindToScore(GameScore score) {
        if (gameViewController != null) {
            gameViewController.bindToScore(score);
        }
    }

    /**
     * Handles keyboard input events.
     */
    public void handleKeyEvent(KeyEvent keyEvent) {
        if (gameViewController != null) {
            gameViewController.handleKeyEvent(keyEvent);
        }
    }

    /**
     * Initializes the game view with board matrix and brick data.
     */
    public void initGameView(int[][] boardMatrix, ViewData brick) {
        if (gameViewController != null) {
            gameViewController.initGameView(boardMatrix, brick);
        }
    }

    /**
     * Refreshes the game background.
     */
    public void refreshGameBackground(int[][] board) {
        if (gameViewController != null) {
            gameViewController.refreshGameBackground(board);
        }
    }

    /**
     * Sets the game speed.
     */
    public void setGameSpeed(Duration duration) {
        if (gameViewController != null) {
            gameViewController.setGameSpeed(duration);
        }
    }

    /**
     * Sets the color palette.
     */
    public void setColorPalette(BrickColorPalette palette) {
        this.palette = palette;
        if (gameViewController != null) {
            gameViewController.setColorPalette(palette);
        }
    }

    /**
     * Sets the event listener.
     */
    public void setEventListener(InputActionListener eventListener) {
        if (gameViewController != null) {
            gameViewController.setEventListener(eventListener);
        }
    }

    /**
     * Sets the final score.
     */
    public void setFinalScore(int score) {
        if (gameViewController != null) {
            gameViewController.setFinalScore(score);
        }
    }

    /**
     * Triggers game over.
     */
    public void gameOver() {
        if (gameViewController != null) {
            gameViewController.gameOver();
        }
    }

    /**
     * Animates cleared rows.
     */
    public void animateClearedRows(int[][] prevMatrix, ClearRow clearRow) {
        if (gameViewController != null) {
            gameViewController.animateClearedRows(prevMatrix, clearRow);
        }
    }

    /**
     * Sets the callback for returning to main menu.
     */
    public void setOnReturnToMainMenu(Runnable r) {
        if (gameLifecycleController != null) {
            gameLifecycleController.setOnReturnToMainMenu(r);
        }
    }

    /**
     * Starts a new game.
     */
    public void newGame(ActionEvent actionEvent) {
        if (gameLifecycleController != null) {
            gameLifecycleController.newGame(actionEvent);
        }
    }

    /**
     * Pauses the game.
     */
    public void pauseGame(ActionEvent actionEvent) {
        if (gameLifecycleController != null) {
            gameLifecycleController.pauseGame(actionEvent);
        }
    }

    /**
     * Sets the game mode.
     */
    public void setGameMode(GameMode mode) {
        if (gameViewController != null) {
            gameViewController.setGameMode(mode);
        }
    }

    /**
     * Sets the GameViewController.
     * Used for dependency injection, especially in tests.
     * @param gameViewController The GameViewController to set.
     */
    public void setGameViewController(GameViewController gameViewController) {
        this.gameViewController = gameViewController;
    }
}
