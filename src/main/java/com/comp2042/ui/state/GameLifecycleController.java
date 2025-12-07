package com.comp2042.ui.state;

import com.comp2042.input.InputActionListener;
import com.comp2042.logic.game.GameLoop;
import com.comp2042.logic.game.GameMode;
import com.comp2042.logic.game.GameSettings;
import com.comp2042.logic.game.ViewData;
import com.comp2042.ui.GameBoardView;
import com.comp2042.ui.overlay.GameOverPanel;
import com.comp2042.ui.overlay.NotificationPanel;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.Random;

/**
 * GameLifecycleController is solely responsible for managing the game lifecycle:
 * - Starting a game (initGameView)
 * - Handling game over state
 * - Pausing and resuming (including countdown)
 * - Switching game modes
 * - Wiring lifecycle-related listeners and callbacks (including "return to main menu")
 * 
 * This controller does NOT handle:
 * - Board animations (handled by BoardAnimationController)
 * - Score display (handled by ScoreUiController)
 * - Input handling (handled by GameInputController)
 * - Background effects (handled by BackgroundEffectController)
 */
public class GameLifecycleController {
    
    private final GridPane gamePanel;
    private final AnchorPane groupNotification;
    private final GameOverPanel gameOverPanel;
    private final javafx.scene.layout.Pane rootPane;
    private final StackPane boardStack;
    
    private InputActionListener eventListener;
    private GameBoardView gameBoardView;
    private GameLoop gameLoop;
    private Runnable onReturnToMainMenu;
    
    private final BooleanProperty isPause = new SimpleBooleanProperty();
    private final BooleanProperty isGameOver = new SimpleBooleanProperty();
    
    private Timeline resumeCountdown;
    
    // Pause overlay UI
    private StackPane pauseOverlay;
    private Label pauseLabel;
    private Button pauseResumeButton;
    private Button pauseMainMenuButton;
    private Button pauseRestartButton;
    
    // Game over overlay UI
    private StackPane gameOverOverlay;
    private Label gameOverTitleLabel;
    private Label gameOverScoreLabel;
    private Button gameOverRestartButton;
    private Button gameOverMainMenuButton;
    
    private AudioClip gameOverSound;
    
    // Danger mode fields
    private Timeline dangerFlashTimer;
    private Rectangle dangerFlashOverlay;
    private Random dangerRandom = new Random();
    private static final double DANGER_FLASH_MIN_SEC = 4.0;
    private static final double DANGER_FLASH_MAX_SEC = 7.0;
    
    private Timeline dangerBoostTimer;
    private Timeline dangerBoostRevertTimer;
    private boolean dangerBoostActive = false;
    private static final double DANGER_BOOST_MIN_SEC = 6.0;
    private static final double DANGER_BOOST_MAX_SEC = 14.0;
    private static final double DANGER_BOOST_DURATION_SEC = 1.8;
    private static final double DANGER_BOOST_MIN_FACTOR = 0.10;
    private static final double DANGER_BOOST_MAX_FACTOR = 0.28;
    private double currentBaseIntervalMs = 400.0;
    
    private Timeline dangerControlTimer;
    private Timeline dangerControlActivateTimer;
    private Timeline dangerControlRevertTimer;
    private boolean controlsFlipped = false;
    private static final double DANGER_CONTROL_MIN_SEC = 7.0;
    private static final double DANGER_CONTROL_MAX_SEC = 18.0;
    private static final double DANGER_CONTROL_DURATION_SEC = 3.5;
    
    // Timer management (delegated to GameTimerController)
    private GameTimerController gameTimerController;
    
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
        this.gameOverPanel = gameOverPanel;
        this.rootPane = rootPane;
        this.boardStack = boardStack;
        this.gameTimerController = new GameTimerController(timerLabel);
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
     * Initializes the pause and game over overlays.
     */
    public void initializeOverlays() {
        createPauseOverlay();
        createGameOverOverlay();
        wireGameOverButtons();
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
        cancelResumeCountdown();
        stopDangerFlashTimer();
        stopDangerBoostTimer();
        stopDangerControlTimer();
        if (gameOverOverlay != null) gameOverOverlay.setVisible(false);
        gameOverPanel.setVisible(false);
        eventListener.createNewGame();
        gamePanel.requestFocus();
        gameLoop.play();
        resetTimer();
        startTimer();
        isPause.setValue(Boolean.FALSE);
        hidePauseOverlay();
        isGameOver.setValue(Boolean.FALSE);
    }
    
    /**
     * Handles game over state.
     */
    public void gameOver() {
        gameLoop.stop();
        stopTimer();
        cancelResumeCountdown();
        stopDangerFlashTimer();
        stopDangerBoostTimer();
        stopDangerControlTimer();
        hidePauseOverlay();
        
        // Play game over sound
        if (gameOverSound != null) {
            gameOverSound.setVolume(GameSettings.getVolume() / 100.0);
            gameOverSound.play();
        }

        if (gameOverOverlay != null) {
            gameOverOverlay.setVisible(true);
        } else {
            gameOverPanel.setVisible(true);
        }
        isGameOver.setValue(Boolean.TRUE);
    }
    
    /**
     * Sets the final score for the game over screen.
     */
    public void setFinalScore(int score) {
        try {
            if (gameOverPanel != null) {
                gameOverPanel.setFinalScore(score);
            }
            if (gameOverScoreLabel != null) {
                gameOverScoreLabel.setText(String.format("SCORE: %d", score));
            }
        } catch (Exception ignored) {
        }
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
        if (isGameOver.getValue()) {
            return;
        }
        if (!isPause.getValue()) {
            isPause.setValue(Boolean.TRUE);
            gameLoop.stop();
            stopTimer();
            cancelResumeCountdown();
            showPauseOverlay();
        } else {
            if (resumeCountdown != null) {
                cancelResumeCountdown();
            } else {
                beginResumeCountdown();
            }
            hidePauseOverlay();
        }
        gamePanel.requestFocus();
    }
    
    /**
     * Begins the resume countdown.
     */
    public void beginResumeCountdown() {
        final int[] remaining = {3};
        resumeCountdown = new Timeline(
                new KeyFrame(Duration.ZERO, e -> showCountdown(String.valueOf(remaining[0]--))),
                new KeyFrame(Duration.seconds(1))
        );
        resumeCountdown.setCycleCount(3);
        resumeCountdown.setOnFinished(e -> {
            isPause.setValue(Boolean.FALSE);
            gameLoop.play();
            startTimer();
            resumeCountdown = null;
        });
        resumeCountdown.playFromStart();
    }
    
    /**
     * Cancels the resume countdown.
     */
    public void cancelResumeCountdown() {
        if (resumeCountdown != null) {
            resumeCountdown.stop();
            resumeCountdown = null;
        }
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
        if (!isPause.getValue() && !isGameOver.getValue()) {
            gameLoop.play();
        }
        // Danger mode visual-only effect: occasional white flash at random intervals
        if (mode == GameMode.DANGER) {
            startDangerFlashTimer();
            startDangerBoostTimer();
            startDangerControlTimer();
        } else {
            stopDangerFlashTimer();
            stopDangerBoostTimer();
            stopDangerControlTimer();
        }
        // record current base interval for boost computations
        currentBaseIntervalMs = mode.getDropIntervalMs();
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
        this.onReturnToMainMenu = r;
    }
    
    // Getter methods for state
    public BooleanProperty isPauseProperty() {
        return isPause;
    }
    
    public BooleanProperty isGameOverProperty() {
        return isGameOver;
    }
    
    public boolean isControlsFlipped() {
        return controlsFlipped;
    }
    
    public GameLoop getGameLoop() {
        return gameLoop;
    }
    
    // Private helper methods
    
    private void showCountdown(String text) {
        NotificationPanel panel = new NotificationPanel(text);
        centerOverlay(panel);
        groupNotification.getChildren().add(panel);
        panel.animateCountdown(groupNotification.getChildren());
    }
    
    private void centerOverlay(NotificationPanel panel) {
        if (gamePanel == null || groupNotification == null) return;

        Bounds boundsInScene = gamePanel.localToScene(gamePanel.getBoundsInLocal());
        double centerX = boundsInScene.getMinX() + boundsInScene.getWidth() / 2;
        double centerY = boundsInScene.getMinY() + boundsInScene.getHeight() / 2;

        Point2D centerInParent = groupNotification.sceneToLocal(centerX, centerY);

        panel.setLayoutX(centerInParent.getX() - panel.getMinWidth() / 2);
        panel.setLayoutY(centerInParent.getY() - panel.getMinHeight() / 2);
    }
    
    private void createPauseOverlay() {
        pauseOverlay = new StackPane();
        pauseOverlay.setVisible(false);
        pauseOverlay.setPickOnBounds(true);

        Rectangle rect = new Rectangle();
        rect.setFill(Color.rgb(0, 0, 0, 0.65));
        if (rootPane != null) {
            rect.widthProperty().bind(rootPane.widthProperty());
            rect.heightProperty().bind(rootPane.heightProperty());
        } else {
            rect.setWidth(215);
            rect.setHeight(520);
        }
        rect.getStyleClass().add("pause-overlay");

        VBox content = new VBox(16);
        content.setAlignment(javafx.geometry.Pos.CENTER);
        pauseLabel = new Label("PAUSED");
        pauseLabel.getStyleClass().add("pauseLabel");
        pauseResumeButton = new Button("Resume");
        pauseResumeButton.getStyleClass().addAll("menu-button", "resume-button", "pause-button");
        pauseResumeButton.setOnAction(e -> togglePause());
        pauseResumeButton.setPrefWidth(180);
        pauseResumeButton.setMinWidth(180);
        pauseResumeButton.setMaxWidth(180);
        pauseResumeButton.setPrefHeight(48);
        pauseRestartButton = new Button("Restart");
        pauseRestartButton.getStyleClass().addAll("menu-button", "restart-button", "pause-button");
        pauseRestartButton.setPrefWidth(180);
        pauseRestartButton.setMinWidth(180);
        pauseRestartButton.setMaxWidth(180);
        pauseRestartButton.setPrefHeight(48);
        pauseRestartButton.setOnAction(e -> newGame(null));

        pauseMainMenuButton = new Button("Main Menu");
        pauseMainMenuButton.getStyleClass().addAll("menu-button", "quit-button", "pause-button");
        pauseMainMenuButton.setPrefWidth(180);
        pauseMainMenuButton.setMinWidth(180);
        pauseMainMenuButton.setMaxWidth(180);
        pauseMainMenuButton.setPrefHeight(48);
        pauseMainMenuButton.setOnAction(e -> {
            if (onReturnToMainMenu != null) {
                onReturnToMainMenu.run();
            }
        });
        HBox buttons = new HBox(20, pauseResumeButton, pauseRestartButton, pauseMainMenuButton);
        buttons.setAlignment(javafx.geometry.Pos.CENTER);
        buttons.setPadding(new javafx.geometry.Insets(0, 20, 0, 20));
        content.getChildren().addAll(pauseLabel, buttons);
        pauseOverlay.getChildren().addAll(rect, content);
        if (rootPane != null) {
            rootPane.getChildren().add(pauseOverlay);
        } else if (groupNotification != null) {
            groupNotification.getChildren().add(pauseOverlay);
        }
    }
    
    private void showPauseOverlay() {
        if (pauseOverlay == null) return;
        pauseOverlay.setVisible(true);
    }

    private void hidePauseOverlay() {
        if (pauseOverlay == null) return;
        pauseOverlay.setVisible(false);
    }
    
    private void createGameOverOverlay() {
        gameOverOverlay = new StackPane();
        gameOverOverlay.setVisible(false);
        gameOverOverlay.setPickOnBounds(true);

        Rectangle rect = new Rectangle();
        rect.setFill(Color.rgb(0, 0, 0, 0.65));
        if (rootPane != null) {
            rect.widthProperty().bind(rootPane.widthProperty());
            rect.heightProperty().bind(rootPane.heightProperty());
        } else {
            rect.setWidth(215);
            rect.setHeight(520);
        }
        rect.getStyleClass().add("game-over-overlay");

        VBox content = new VBox(16);
        content.setAlignment(javafx.geometry.Pos.CENTER);

        gameOverTitleLabel = new Label("GAME OVER");
        gameOverTitleLabel.getStyleClass().add("gameOverStyle");
        gameOverTitleLabel.setWrapText(true);

        gameOverScoreLabel = new Label("SCORE: 0");
        gameOverScoreLabel.getStyleClass().add("finalScoreBox");

        gameOverRestartButton = new Button("Restart");
        gameOverRestartButton.getStyleClass().addAll("menu-button", "restart-button", "pause-button");
        gameOverRestartButton.setPrefWidth(180);
        gameOverRestartButton.setMinWidth(180);
        gameOverRestartButton.setMaxWidth(180);
        gameOverRestartButton.setPrefHeight(48);
        gameOverRestartButton.setOnAction(e -> newGame(null));

        gameOverMainMenuButton = new Button("Main Menu");
        gameOverMainMenuButton.getStyleClass().addAll("menu-button", "main-menu-button", "pause-button");
        gameOverMainMenuButton.setPrefWidth(180);
        gameOverMainMenuButton.setMinWidth(180);
        gameOverMainMenuButton.setMaxWidth(180);
        gameOverMainMenuButton.setPrefHeight(48);
        gameOverMainMenuButton.setOnAction(e -> { if (onReturnToMainMenu != null) onReturnToMainMenu.run(); });

        HBox buttons = new HBox(20, gameOverRestartButton, gameOverMainMenuButton);
        buttons.setAlignment(javafx.geometry.Pos.CENTER);
        buttons.setPadding(new javafx.geometry.Insets(0, 20, 0, 20));

        content.getChildren().addAll(gameOverTitleLabel, gameOverScoreLabel, buttons);
        gameOverOverlay.getChildren().addAll(rect, content);

        if (rootPane != null) {
            rootPane.getChildren().add(gameOverOverlay);
        } else if (groupNotification != null) {
            groupNotification.getChildren().add(gameOverOverlay);
        }
    }
    
    private void wireGameOverButtons() {
        if (gameOverPanel == null) return;
        gameOverPanel.setOnRestart(() -> {
            newGame(null);
        });
        gameOverPanel.setOnMainMenu(() -> {
            if (onReturnToMainMenu != null) {
                onReturnToMainMenu.run();
            }
        });

        if (gameOverRestartButton != null) {
            gameOverRestartButton.setOnAction(e -> newGame(null));
        }
        if (gameOverMainMenuButton != null) {
            gameOverMainMenuButton.setOnAction(e -> { if (onReturnToMainMenu != null) onReturnToMainMenu.run(); });
        }
    }
    
    // Danger mode methods
    
    private void startDangerFlashTimer() {
        if (dangerFlashTimer != null) {
            return;
        }
        if (dangerFlashOverlay == null) {
            dangerFlashOverlay = new Rectangle();
            dangerFlashOverlay.setFill(Color.WHITE);
            dangerFlashOverlay.setOpacity(0);
            dangerFlashOverlay.setMouseTransparent(true);
            dangerFlashOverlay.widthProperty().bind(gamePanel.widthProperty());
            dangerFlashOverlay.heightProperty().bind(gamePanel.heightProperty());
        }
        if (boardStack != null) {
            if (!boardStack.getChildren().contains(dangerFlashOverlay)) {
                boardStack.getChildren().add(dangerFlashOverlay);
                StackPane.setAlignment(dangerFlashOverlay, javafx.geometry.Pos.TOP_LEFT);
            }
        } else if (!groupNotification.getChildren().contains(dangerFlashOverlay)) {
            groupNotification.getChildren().add(dangerFlashOverlay);
        }
        scheduleNextDangerFlash();
    }

    private void scheduleNextDangerFlash() {
        if (dangerFlashTimer != null) {
            dangerFlashTimer.stop();
            dangerFlashTimer = null;
        }
        double delay = DANGER_FLASH_MIN_SEC + dangerRandom.nextDouble() * (DANGER_FLASH_MAX_SEC - DANGER_FLASH_MIN_SEC);
        dangerFlashTimer = new Timeline(new KeyFrame(Duration.seconds(delay), e -> {
            flashDangerOverlay();
            scheduleNextDangerFlash();
        }));
        dangerFlashTimer.setCycleCount(1);
        dangerFlashTimer.play();
    }

    private void stopDangerFlashTimer() {
        if (dangerFlashTimer != null) {
            dangerFlashTimer.stop();
            dangerFlashTimer = null;
        }
        if (dangerFlashOverlay != null) {
            if (boardStack != null && boardStack.getChildren().contains(dangerFlashOverlay)) {
                boardStack.getChildren().remove(dangerFlashOverlay);
            } else if (groupNotification != null && groupNotification.getChildren().contains(dangerFlashOverlay)) {
                groupNotification.getChildren().remove(dangerFlashOverlay);
            }
        }
    }

    private void startDangerBoostTimer() {
        if (dangerBoostTimer != null) {
            return;
        }
        scheduleNextDangerBoost();
    }

    private void scheduleNextDangerBoost() {
        if (dangerBoostTimer != null) {
            dangerBoostTimer.stop();
            dangerBoostTimer = null;
        }
        double delay = DANGER_BOOST_MIN_SEC + dangerRandom.nextDouble() * (DANGER_BOOST_MAX_SEC - DANGER_BOOST_MIN_SEC);
        dangerBoostTimer = new Timeline(new KeyFrame(Duration.seconds(delay), e -> {
            if (isPause.getValue() || isGameOver.getValue()) {
                scheduleNextDangerBoost();
                return;
            }
            applyDangerBoost();
        }));
        dangerBoostTimer.setCycleCount(1);
        dangerBoostTimer.play();
    }

    private void applyDangerBoost() {
        if (dangerBoostActive || gameLoop == null) {
            scheduleNextDangerBoost();
            return;
        }
        dangerBoostActive = true;
        final double baseMs = currentBaseIntervalMs;
        final double factor = DANGER_BOOST_MIN_FACTOR + dangerRandom.nextDouble() * (DANGER_BOOST_MAX_FACTOR - DANGER_BOOST_MIN_FACTOR);
        final double boostedMs = Math.max(50, baseMs * factor);
        gameLoop.updateInterval(Duration.millis(boostedMs));
        if (dangerBoostRevertTimer != null) {
            dangerBoostRevertTimer.stop();
            dangerBoostRevertTimer = null;
        }
        double duration = DANGER_BOOST_DURATION_SEC + (dangerRandom.nextDouble() - 0.5) * 1.0;
        dangerBoostRevertTimer = new Timeline(new KeyFrame(Duration.seconds(duration), ev -> {
            revertDangerBoost();
        }));
        dangerBoostRevertTimer.setCycleCount(1);
        dangerBoostRevertTimer.play();
    }

    private void revertDangerBoost() {
        if (!dangerBoostActive) {
            return;
        }
        dangerBoostActive = false;
        gameLoop.updateInterval(Duration.millis(currentBaseIntervalMs));
        if (dangerBoostRevertTimer != null) {
            dangerBoostRevertTimer.stop();
            dangerBoostRevertTimer = null;
        }
        scheduleNextDangerBoost();
    }

    private void stopDangerBoostTimer() {
        if (dangerBoostTimer != null) {
            dangerBoostTimer.stop();
            dangerBoostTimer = null;
        }
        if (dangerBoostRevertTimer != null) {
            dangerBoostRevertTimer.stop();
            dangerBoostRevertTimer = null;
        }
        if (dangerBoostActive) {
            dangerBoostActive = false;
            gameLoop.updateInterval(Duration.millis(currentBaseIntervalMs));
        }
    }

    private void startDangerControlTimer() {
        if (dangerControlTimer != null) {
            return;
        }
        scheduleNextDangerControl();
    }

    private void scheduleNextDangerControl() {
        if (dangerControlTimer != null) {
            dangerControlTimer.stop();
            dangerControlTimer = null;
        }
        double delay = DANGER_CONTROL_MIN_SEC + dangerRandom.nextDouble() * (DANGER_CONTROL_MAX_SEC - DANGER_CONTROL_MIN_SEC);
        dangerControlTimer = new Timeline(new KeyFrame(Duration.seconds(delay), e -> {
            if (isPause.getValue() || isGameOver.getValue()) {
                scheduleNextDangerControl();
                return;
            }
            if (!isPause.getValue() && !isGameOver.getValue()) {
                showControlWarning("CONTROLS\nSWITCHED!");
                if (dangerControlActivateTimer != null) {
                    dangerControlActivateTimer.stop();
                    dangerControlActivateTimer = null;
                }
                dangerControlActivateTimer = new Timeline(new KeyFrame(Duration.millis(650), ev -> applyDangerControlFlip()));
                dangerControlActivateTimer.setCycleCount(1);
                dangerControlActivateTimer.play();
            } else {
                applyDangerControlFlip();
            }
        }));
        dangerControlTimer.setCycleCount(1);
        dangerControlTimer.play();
    }

    private void applyDangerControlFlip() {
        if (controlsFlipped) {
            scheduleNextDangerControl();
            return;
        }
        controlsFlipped = true;
        if (dangerControlRevertTimer != null) {
            dangerControlRevertTimer.stop();
            dangerControlRevertTimer = null;
        }
        double duration = DANGER_CONTROL_DURATION_SEC + (dangerRandom.nextDouble() - 0.5) * 1.5;
        if (duration < 0.8) duration = DANGER_CONTROL_DURATION_SEC;
        dangerControlRevertTimer = new Timeline(new KeyFrame(Duration.seconds(duration), ev -> {
            revertDangerControlFlip();
        }));
        dangerControlRevertTimer.setCycleCount(1);
        dangerControlRevertTimer.play();
    }

    private void revertDangerControlFlip() {
        if (!controlsFlipped) {
            return;
        }
        controlsFlipped = false;
        if (dangerControlRevertTimer != null) {
            dangerControlRevertTimer.stop();
            dangerControlRevertTimer = null;
        }
        scheduleNextDangerControl();
    }

    private void stopDangerControlTimer() {
        if (dangerControlTimer != null) {
            dangerControlTimer.stop();
            dangerControlTimer = null;
        }
        if (dangerControlRevertTimer != null) {
            dangerControlRevertTimer.stop();
            dangerControlRevertTimer = null;
        }
        if (controlsFlipped) {
            controlsFlipped = false;
        }
        if (dangerControlActivateTimer != null) {
            dangerControlActivateTimer.stop();
            dangerControlActivateTimer = null;
        }
    }

    private void showControlWarning(String message) {
        if (isPause.getValue() || isGameOver.getValue()) return;
        Label bubble = new Label(message);
        bubble.getStyleClass().add("control-warning");
        bubble.setWrapText(true);
        bubble.setMaxWidth(300);
        
        groupNotification.getChildren().add(bubble);
        bubble.applyCss();
        bubble.layout();

        if (gamePanel != null) {
            Bounds boundsInScene = gamePanel.localToScene(gamePanel.getBoundsInLocal());
            double centerX = boundsInScene.getMinX() + boundsInScene.getWidth() / 2;
            double centerY = boundsInScene.getMinY() + boundsInScene.getHeight() / 2;
            
            Point2D centerInParent = groupNotification.sceneToLocal(centerX, centerY);
            
            bubble.setLayoutX(centerInParent.getX() - bubble.getWidth() / 2);
            bubble.setLayoutY(centerInParent.getY() - bubble.getHeight() / 2);
        }

        FadeTransition ft = new FadeTransition(Duration.millis(500), bubble);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        ft.setDelay(Duration.millis(1500));
        ft.setOnFinished(e -> groupNotification.getChildren().remove(bubble));
        ft.play();
    }

    private void flashDangerOverlay() {
        if (dangerFlashOverlay == null) {
            return;
        }
        FadeTransition in = new FadeTransition(Duration.millis(120), dangerFlashOverlay);
        in.setFromValue(0);
        in.setToValue(0.95);
        in.setCycleCount(1);
        in.setOnFinished(e -> {
            javafx.animation.PauseTransition hold = new javafx.animation.PauseTransition(Duration.millis(400));
            hold.setOnFinished(ev -> {
                FadeTransition out = new FadeTransition(Duration.millis(300), dangerFlashOverlay);
                out.setFromValue(0.95);
                out.setToValue(0);
                out.play();
            });
            hold.play();
        });
        in.play();
    }
}
