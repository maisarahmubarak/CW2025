package com.comp2042.ui;

import com.comp2042.BrickColorPalette;
import com.comp2042.ClassicBrickPalette;
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

    private static int highScore = 0;
    private static final String HIGH_SCORE_FILE = "highscore.dat";

    @FXML
    private VBox timerPanel;

    @FXML
    private Label timerLabel;

    private Timeline timerTimeline;
    private int elapsedSeconds = 0;

    private GameStateController gameStateController;
    private InputActionListener eventListener;
    private GameBoardView gameBoardView;
    private GameLoop gameLoop;
    private BrickColorPalette palette = new ClassicBrickPalette();
    private Random dangerRandom = new Random(); // Used for visual animation effects

    private AudioClip rowClearSound;
    private AudioClip gameOverSound;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadHighScore();
        Font.loadFont(getClass().getClassLoader().getResource("digital.ttf").toExternalForm(), 38);
        
        // Load sounds
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



    public void bindToScore(GameScore score) {
        if (score == null) {
            return;
        }
        scoreLabel.textProperty().bind(score.scoreProperty().asString());
        score.scoreProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.intValue() > highScore) {
                highScore = newVal.intValue();
                updateHighScoreLabel();
                saveHighScore();
            }
        });
        updateHighScoreLabel();
    }

    private void updateHighScoreLabel() {
        if (highScoreLabel != null) {
            highScoreLabel.setText(String.valueOf(highScore));
        }
    }

    private void loadHighScore() {
        File file = new File(HIGH_SCORE_FILE);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line = reader.readLine();
                if (line != null) {
                    highScore = Integer.parseInt(line.trim());
                }
            } catch (IOException | NumberFormatException e) {
                System.err.println("Failed to load high score: " + e.getMessage());
            }
        }
    }

    private void saveHighScore() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HIGH_SCORE_FILE))) {
            writer.write(String.valueOf(highScore));
        } catch (IOException e) {
            System.err.println("Failed to save high score: " + e.getMessage());
        }
    }

    // Moved the anonymous key handler here so key-handling logic lives in one place
    // and can be delegated to from a separate GameKeyHandler class.
    public void handleKeyEvent(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.P) {
            gameStateController.togglePause();
            keyEvent.consume();
            return;
        }
        if (gameStateController.isPauseProperty().getValue() == Boolean.FALSE && gameStateController.isGameOverProperty().getValue() == Boolean.FALSE) {
            if (keyEvent.getCode() == KeyCode.LEFT || keyEvent.getCode() == KeyCode.A) {
                if (gameStateController.isControlsFlipped()) {
                    gameBoardView.refreshBrick(eventListener.onRightEvent(new MoveAction(ActionType.RIGHT, ActionSource.USER)));
                } else {
                    gameBoardView.refreshBrick(eventListener.onLeftEvent(new MoveAction(ActionType.LEFT, ActionSource.USER)));
                }
                keyEvent.consume();
            }
            if (keyEvent.getCode() == KeyCode.RIGHT || keyEvent.getCode() == KeyCode.D) {
                if (gameStateController.isControlsFlipped()) {
                    gameBoardView.refreshBrick(eventListener.onLeftEvent(new MoveAction(ActionType.LEFT, ActionSource.USER)));
                } else {
                    gameBoardView.refreshBrick(eventListener.onRightEvent(new MoveAction(ActionType.RIGHT, ActionSource.USER)));
                }
                keyEvent.consume();
            }
            if (keyEvent.getCode() == KeyCode.UP || keyEvent.getCode() == KeyCode.W) {
                gameBoardView.refreshBrick(eventListener.onRotateEvent(new MoveAction(ActionType.ROTATE, ActionSource.USER)));
                keyEvent.consume();
            }
            if (keyEvent.getCode() == KeyCode.DOWN || keyEvent.getCode() == KeyCode.S) {
                moveDown(new MoveAction(ActionType.DOWN, ActionSource.USER));
                keyEvent.consume();
            }
        }
        if (keyEvent.getCode() == KeyCode.N) {
            gameStateController.newGame(null);
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
    }

    public void setFinalScore(int score) {
        gameStateController.setFinalScore(score);
    }



    public void gameOver() {
        gameStateController.gameOver();
    }

    /**
     * Visual-only animation: turn cleared row blocks into falling and vanishing rectangles.
     * This does not alter game state; it only animates an overlay based on the previous board snapshot.
     */
    public void animateClearedRows(int[][] prevMatrix, ClearRow clearRow) {
        if (prevMatrix == null || clearRow == null || clearRow.getLinesRemoved() <= 0) return;
        
        // Play sound effect
        if (rowClearSound != null) {
            rowClearSound.setVolume(GameSettings.getVolume() / 100.0);
            rowClearSound.play();
        }

        if (boardStack == null) return;
        if (!(gameBoardView instanceof JavaFxBoardView)) return;
        JavaFxBoardView jfxView = (JavaFxBoardView) gameBoardView;

        int[] rows = clearRow.getClearedRows();
        // visual-only: small vertical shake on the board for multi-row clears (2 or more rows)
        if (clearRow.getLinesRemoved() >= 2 && boardStack != null) {
            // make a small amplitude based on lines removed: 2 -> 6px, 3 -> 8px, 4 -> 10px
            final double amplitude = 6.0 + Math.max(0, clearRow.getLinesRemoved() - 2) * 2.0;
            Platform.runLater(() -> {
                // Keyframe timeline creates a quick vertical shake (up / down / settle)
                Timeline shake = new Timeline(
                        new KeyFrame(Duration.ZERO, new KeyValue(boardStack.translateYProperty(), 0)),
                        new KeyFrame(Duration.millis(40), new KeyValue(boardStack.translateYProperty(), -amplitude)),
                        new KeyFrame(Duration.millis(80), new KeyValue(boardStack.translateYProperty(), amplitude)),
                        new KeyFrame(Duration.millis(120), new KeyValue(boardStack.translateYProperty(), -amplitude / 2.0)),
                        new KeyFrame(Duration.millis(160), new KeyValue(boardStack.translateYProperty(), 0))
                );
                shake.setCycleCount(1);
                shake.play();
            });
        }
        System.out.println("Animating cleared rows: " + rows.length + " rows");
        for (int r : rows) {
            for (int c = 0; c < prevMatrix[0].length; c++) {
                int color = prevMatrix[r][c];
                if (color == 0) continue;
                Rectangle rect = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rect.setFill(palette.colorFor(color));
                rect.setArcHeight(0);
                rect.setArcWidth(0);
                rect.setMouseTransparent(true);
                // compute position inside boardStack
                javafx.geometry.Point2D scenePoint = jfxView.getCellScenePosition(c, r);
                javafx.geometry.Point2D local;
                if (groupNotification != null) {
                    local = groupNotification.sceneToLocal(scenePoint);
                    // skip cells above visible region (hidden rows)
                    if (local.getY() < -8) {
                        continue;
                    }
                    // Position the rect absolutely within groupNotification so it's above everything
                    rect.setLayoutX(local.getX());
                    rect.setLayoutY(local.getY());
                    groupNotification.getChildren().add(rect);
                } else if (boardStack != null) {
                    local = boardStack.sceneToLocal(scenePoint);
                    if (local.getY() < -8) {
                        continue;
                    }
                    StackPane.setAlignment(rect, javafx.geometry.Pos.TOP_LEFT);
                    rect.setTranslateX(local.getX());
                    rect.setTranslateY(local.getY());
                    boardStack.getChildren().add(rect);
                } else {
                    // no place to draw overlay; skip
                    continue;
                }

                // animation: fall a longer amount and fade out with a slight stagger + horizontal spread and rotation
                // shorten the durations and delays to make the clear animation quicker while preserving visuals
                TranslateTransition tt = new TranslateTransition(Duration.millis(350), rect);
                tt.setByY(BRICK_SIZE * 2.0); // longer fall
                // small horizontal spread: random lateral byX to spread the falling bricks slightly
                double spreadPx = (dangerRandom.nextDouble() - 0.5) * BRICK_SIZE * 1.2; // ±12px range
                tt.setByX(spreadPx);
                FadeTransition ft = new FadeTransition(Duration.millis(320), rect);
                ft.setFromValue(1.0);
                ft.setToValue(0.0);
                long baseDelay = 40; // reduced base delay
                long stagger = (c * 14) + (r % 3) * 6; // lateral + small row-based offset (reduced)
                ft.setDelay(Duration.millis(baseDelay + stagger));
                tt.setDelay(Duration.millis(baseDelay + stagger));
                // Add a gentle rotation so pieces twist as they fall
                RotateTransition rt = new RotateTransition(Duration.millis(350), rect);
                rt.setByAngle((dangerRandom.nextDouble() - 0.5) * 36.0); // -18 .. +18 deg
                rt.setDelay(Duration.millis(baseDelay + stagger));
                ParallelTransition pt = new ParallelTransition(tt, ft, rt);
                pt.setOnFinished(ev -> {
                    try {
                        boardStack.getChildren().remove(rect);
                    } catch (Exception ignored) {
                    }
                });
                pt.play();
            }
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
