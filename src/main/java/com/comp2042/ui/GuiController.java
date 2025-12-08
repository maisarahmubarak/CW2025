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
 * Main GUI controller coordinating UI elements.
 * <p>
 * GuiController manages FXML bindings and initializes game components.
 * Responsibilities:
 * <ul>
 *   <li>FXML component binding and initialization</li>
 *   <li>Creating and wiring controllers (lifecycle, input, animation, etc.)</li>
 *   <li>Loading resources (fonts, sounds)</li>
 *   <li>Delegating game operations to GameViewController</li>
 * </ul>
 * </p>
 *
 * @author Maisarah
 * @version 1.0
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

    /**
     * Initializes the controller class.
     * <p>
     * Sets up the game board, input handling, sound effects, and various sub-controllers
     * (lifecycle, input, animation, background effects). It also configures the game loop
     * and binds UI components to their respective logic.
     * </p>
     *
     * @param location  The location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resources The resources used to localize the root object, or null if the root object was not localized.
     */
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

    /**
     * Applies the brightness setting to the root pane.
     * <p>
     * Retrieves the brightness value from GameSettings and applies a ColorAdjust effect.
     * </p>
     */
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
     * Binds the score display to the game score property.
     * <p>
     * Delegates to the GameViewController to update the score UI when the score changes.
     * </p>
     *
     * @param score the GameScore object to bind to.
     */
    public void bindToScore(GameScore score) {
        if (gameViewController != null) {
            gameViewController.bindToScore(score);
        }
    }

    /**
     * Handles keyboard input events from the user.
     * <p>
     * Delegates the key event to the GameViewController for processing.
     * </p>
     *
     * @param keyEvent the KeyEvent to handle.
     */
    public void handleKeyEvent(KeyEvent keyEvent) {
        if (gameViewController != null) {
            gameViewController.handleKeyEvent(keyEvent);
        }
    }

    /**
     * Initializes the game view with the initial board matrix and active brick.
     * <p>
     * Delegates to the GameViewController to set up the visual representation of the game.
     * </p>
     *
     * @param boardMatrix the initial state of the game board.
     * @param brick the initial active brick view data.
     */
    public void initGameView(int[][] boardMatrix, ViewData brick) {
        if (gameViewController != null) {
            gameViewController.initGameView(boardMatrix, brick);
        }
    }

    /**
     * Refreshes the game background based on the current board state.
     * <p>
     * Delegates to the GameViewController to update the grid display.
     * </p>
     *
     * @param board the current board matrix.
     */
    public void refreshGameBackground(int[][] board) {
        if (gameViewController != null) {
            gameViewController.refreshGameBackground(board);
        }
    }

    /**
     * Sets the speed of the game loop.
     * <p>
     * Delegates to the GameViewController to adjust the drop interval.
     * </p>
     *
     * @param duration the new duration for the game loop interval.
     */
    public void setGameSpeed(Duration duration) {
        if (gameViewController != null) {
            gameViewController.setGameSpeed(duration);
        }
    }

    /**
     * Sets the color palette for the bricks.
     * <p>
     * Updates the local palette reference and delegates to the GameViewController.
     * </p>
     *
     * @param palette the BrickColorPalette to use.
     */
    public void setColorPalette(BrickColorPalette palette) {
        this.palette = palette;
        if (gameViewController != null) {
            gameViewController.setColorPalette(palette);
        }
    }

    /**
     * Sets the event listener for game actions.
     * <p>
     * Delegates to the GameViewController to register the listener.
     * </p>
     *
     * @param eventListener the InputActionListener to receive game events.
     */
    public void setEventListener(InputActionListener eventListener) {
        if (gameViewController != null) {
            gameViewController.setEventListener(eventListener);
        }
    }

    /**
     * Sets the final score to be displayed on the game over screen.
     * <p>
     * Delegates to the GameViewController.
     * </p>
     *
     * @param score the final score achieved.
     */
    public void setFinalScore(int score) {
        if (gameViewController != null) {
            gameViewController.setFinalScore(score);
        }
    }

    /**
     * Triggers the game over state.
     * <p>
     * Delegates to the GameViewController to handle game over logic and UI updates.
     * </p>
     */
    public void gameOver() {
        if (gameViewController != null) {
            gameViewController.gameOver();
        }
    }

    /**
     * Animates the clearing of rows.
     * <p>
     * Delegates to the GameViewController to perform the visual effects for cleared rows.
     * </p>
     *
     * @param prevMatrix the board matrix before the rows were cleared.
     * @param clearRow the object containing details about the cleared rows.
     */
    public void animateClearedRows(int[][] prevMatrix, ClearRow clearRow) {
        if (gameViewController != null) {
            gameViewController.animateClearedRows(prevMatrix, clearRow);
        }
    }

    /**
     * Sets the callback to be executed when returning to the main menu.
     * <p>
     * Delegates to the GameLifecycleController.
     * </p>
     *
     * @param r the Runnable to execute.
     */
    public void setOnReturnToMainMenu(Runnable r) {
        if (gameLifecycleController != null) {
            gameLifecycleController.setOnReturnToMainMenu(r);
        }
    }

    /**
     * Starts a new game.
     * <p>
     * Delegates to the GameLifecycleController to reset the game state.
     * </p>
     *
     * @param actionEvent the event that triggered the new game action.
     */
    public void newGame(ActionEvent actionEvent) {
        if (gameLifecycleController != null) {
            gameLifecycleController.newGame(actionEvent);
        }
    }

    /**
     * Pauses or resumes the game.
     * <p>
     * Delegates to the GameLifecycleController to toggle the pause state.
     * </p>
     *
     * @param actionEvent the event that triggered the pause action.
     */
    public void pauseGame(ActionEvent actionEvent) {
        if (gameLifecycleController != null) {
            gameLifecycleController.pauseGame(actionEvent);
        }
    }

    /**
     * Sets the game mode for the current session.
     * <p>
     * Delegates to the GameViewController to configure mode-specific settings.
     * </p>
     *
     * @param mode the GameMode to set.
     */
    public void setGameMode(GameMode mode) {
        if (gameViewController != null) {
            gameViewController.setGameMode(mode);
        }
    }

    /**
     * Sets the GameViewController instance.
     * <p>
     * This method is primarily used for dependency injection, particularly in testing scenarios.
     * </p>
     *
     * @param gameViewController The GameViewController to set.
     */
    public void setGameViewController(GameViewController gameViewController) {
        this.gameViewController = gameViewController;
    }
}
