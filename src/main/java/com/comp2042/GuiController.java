package com.comp2042;

import javafx.animation.KeyFrame;
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

import java.net.URL;
import java.util.ResourceBundle;

public class GuiController implements Initializable {

    private static final int BRICK_SIZE = 20;

    @FXML
    private GridPane gamePanel;

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
    private Runnable onReturnToMainMenu;
    
    @FXML
    private VBox scorePanel;

    @FXML
    private Label scoreLabel;

    @FXML
    private VBox timerPanel;

    @FXML
    private Label timerLabel;

    private Timeline timerTimeline;
    private int elapsedSeconds = 0;

    private InputActionListener eventListener;

    private GameBoardView gameBoardView;

    private GameLoop gameLoop;

    private BrickColorPalette palette = new ClassicBrickPalette();

    private final BooleanProperty isPause = new SimpleBooleanProperty();

    private final BooleanProperty isGameOver = new SimpleBooleanProperty();

    private Timeline resumeCountdown;
    // Danger mode flash UI
    private Timeline dangerFlashTimer;
    private Rectangle dangerFlashOverlay;
    private Random dangerRandom = new Random();
    // Make flashes less frequent: schedule intervals in a wider range
    private static final double DANGER_FLASH_MIN_SEC = 4.0; // minimum delay between flashes (was 1.5)
    private static final double DANGER_FLASH_MAX_SEC = 7.0; // maximum delay between flashes (was 3.0)
    // Danger speed boost constants and state
    private Timeline dangerBoostTimer;
    private Timeline dangerBoostRevertTimer;
    private boolean dangerBoostActive = false;
    private static final double DANGER_BOOST_MIN_SEC = 6.0;
    private static final double DANGER_BOOST_MAX_SEC = 14.0;
    private static final double DANGER_BOOST_DURATION_SEC = 1.8;
    // Randomized boost factor range: lower values make falling faster. Increase intensity slightly
    // New range is ~0.10 .. 0.28 to produce stronger temporary drops in Danger mode.
    private static final double DANGER_BOOST_MIN_FACTOR = 0.10;
    private static final double DANGER_BOOST_MAX_FACTOR = 0.28;
    private double currentBaseIntervalMs = 400.0;
    // Danger mode control flip fields
    private Timeline dangerControlTimer;
    private Timeline dangerControlActivateTimer;
    private Timeline dangerControlRevertTimer;
    private boolean controlsFlipped = false;
    private static final double DANGER_CONTROL_MIN_SEC = 7.0; // min delay between flips
    private static final double DANGER_CONTROL_MAX_SEC = 18.0; // max delay between flips
    private static final double DANGER_CONTROL_DURATION_SEC = 3.5; // how long flip lasts

    // Pause overlay UI
    private StackPane pauseOverlay;
    private javafx.scene.control.Label pauseLabel;
    private javafx.scene.control.Button pauseResumeButton;
    private javafx.scene.control.Button pauseMainMenuButton;

    // Game over overlay UI (mirror of pause overlay)
    private StackPane gameOverOverlay;
    private javafx.scene.control.Label gameOverTitleLabel;
    private javafx.scene.control.Label gameOverScoreLabel;
    private javafx.scene.control.Button gameOverRestartButton;
    private javafx.scene.control.Button gameOverMainMenuButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Font.loadFont(getClass().getClassLoader().getResource("digital.ttf").toExternalForm(), 38);
        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();
        gamePanel.setOnKeyPressed(new GameKeyHandler(this));
        gameBoardView = new GameBoardView(gamePanel, brickPanel, previewPanel, BRICK_SIZE);
        gameBoardView.setPalette(palette);
        brickPanel.toFront();
        gameLoop = new GameLoop(Duration.millis(400), () -> moveDown(new MoveAction(ActionType.DOWN, ActionSource.THREAD)));
        gameOverPanel.setVisible(false);
        createPauseOverlay();
        createGameOverOverlay();
        // Wire the GameOver buttons so they call GUI-level handlers
        wireGameOverButtons();
        // Ensure that the overlay anchor pane fills the whole window, so overlays are fullscreen
        if (groupNotification != null && rootPane != null) {
            groupNotification.prefWidthProperty().bind(rootPane.widthProperty());
            groupNotification.prefHeightProperty().bind(rootPane.heightProperty());
        }
        initTimer();
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
        } else if (gameBoard != null) {
            rect.widthProperty().bind(gameBoard.widthProperty());
            rect.heightProperty().bind(gameBoard.heightProperty());
        } else {
            rect.setWidth(215);
            rect.setHeight(520);
        }
        rect.getStyleClass().add("game-over-overlay");

        VBox content = new VBox(16);
        content.setAlignment(javafx.geometry.Pos.CENTER);

        gameOverTitleLabel = new javafx.scene.control.Label("GAME OVER");
        gameOverTitleLabel.getStyleClass().add("gameOverStyle");
        gameOverTitleLabel.setWrapText(true);

        gameOverScoreLabel = new javafx.scene.control.Label("SCORE: 0");
        gameOverScoreLabel.getStyleClass().add("finalScoreBox");

        gameOverRestartButton = new javafx.scene.control.Button("Restart");
        gameOverRestartButton.getStyleClass().add("menu-button");
        gameOverRestartButton.setOnAction(e -> newGame(null));

        gameOverMainMenuButton = new javafx.scene.control.Button("Main Menu");
        gameOverMainMenuButton.getStyleClass().add("menu-button");
        gameOverMainMenuButton.setOnAction(e -> { if (onReturnToMainMenu != null) onReturnToMainMenu.run(); });

        HBox buttons = new HBox(12, gameOverRestartButton, gameOverMainMenuButton);
        buttons.setAlignment(javafx.geometry.Pos.CENTER);

        content.getChildren().addAll(gameOverTitleLabel, gameOverScoreLabel, buttons);
        gameOverOverlay.getChildren().addAll(rect, content);

        // Add overlay to rootPane so it covers the whole window; fallback to groupNotification
        if (rootPane != null) {
            rootPane.getChildren().add(gameOverOverlay);
        } else if (groupNotification != null) {
            groupNotification.getChildren().add(gameOverOverlay);
        }
    }

    private void createPauseOverlay() {
        pauseOverlay = new StackPane();
        pauseOverlay.setVisible(false);
        pauseOverlay.setPickOnBounds(true);

        javafx.scene.shape.Rectangle rect = new javafx.scene.shape.Rectangle();
        rect.setFill(javafx.scene.paint.Color.rgb(0, 0, 0, 0.65));
        // bind rectangle to the full window rootPane (so it covers whole game window)
        if (rootPane != null) {
            rect.widthProperty().bind(rootPane.widthProperty());
            rect.heightProperty().bind(rootPane.heightProperty());
        } else if (gameBoard != null) {
            // fallback to board area
            rect.widthProperty().bind(gameBoard.widthProperty());
            rect.heightProperty().bind(gameBoard.heightProperty());
        } else {
            rect.setWidth(215);
            rect.setHeight(520);
        }
        rect.getStyleClass().add("pause-overlay");

        VBox content = new VBox(16);
        content.setAlignment(javafx.geometry.Pos.CENTER);
        pauseLabel = new javafx.scene.control.Label("PAUSED");
        pauseLabel.getStyleClass().add("pauseLabel");
        pauseResumeButton = new javafx.scene.control.Button("Resume");
        pauseResumeButton.getStyleClass().add("menu-button");
        pauseResumeButton.setOnAction(e -> togglePause());
        pauseMainMenuButton = new javafx.scene.control.Button("Main Menu");
        pauseMainMenuButton.getStyleClass().add("menu-button");
        pauseMainMenuButton.setOnAction(e -> {
            // Navigate back to main menu via registered callback if available
            if (onReturnToMainMenu != null) {
                onReturnToMainMenu.run();
            }
        });
        HBox buttons = new HBox(12, pauseResumeButton, pauseMainMenuButton);
        buttons.setAlignment(javafx.geometry.Pos.CENTER);
        content.getChildren().addAll(pauseLabel, buttons);
        pauseOverlay.getChildren().addAll(rect, content);
        // Add overlay to rootPane so it covers the whole window; fallback to groupNotification
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

    private void initTimer() {
        timerTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            elapsedSeconds++;
            updateTimerLabel();
        }));
        timerTimeline.setCycleCount(Timeline.INDEFINITE);
    }

    private void updateTimerLabel() {
        int minutes = elapsedSeconds / 60;
        int seconds = elapsedSeconds % 60;
        timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
    }

    private void startTimer() {
        timerTimeline.play();
    }

    private void stopTimer() {
        timerTimeline.stop();
    }

    private void resetTimer() {
        elapsedSeconds = 0;
        updateTimerLabel();
    }

    public void bindToScore(GameScore score) {
        if (score == null) {
            return;
        }
        scoreLabel.textProperty().bind(score.scoreProperty().asString());
    }

    // Moved the anonymous key handler here so key-handling logic lives in one place
    // and can be delegated to from a separate GameKeyHandler class.
    public void handleKeyEvent(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.P) {
            togglePause();
            keyEvent.consume();
            return;
        }
        if (isPause.getValue() == Boolean.FALSE && isGameOver.getValue() == Boolean.FALSE) {
            if (keyEvent.getCode() == KeyCode.LEFT || keyEvent.getCode() == KeyCode.A) {
                if (controlsFlipped) {
                    gameBoardView.refreshBrick(eventListener.onRightEvent(new MoveAction(ActionType.RIGHT, ActionSource.USER)));
                } else {
                    gameBoardView.refreshBrick(eventListener.onLeftEvent(new MoveAction(ActionType.LEFT, ActionSource.USER)));
                }
                keyEvent.consume();
            }
            if (keyEvent.getCode() == KeyCode.RIGHT || keyEvent.getCode() == KeyCode.D) {
                if (controlsFlipped) {
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
            newGame(null);
        }
    }

    public void initGameView(int[][] boardMatrix, ViewData brick) {
        gameBoardView.initGameView(boardMatrix, brick);
        gameLoop.play();
        resetTimer();
        startTimer();
    }

    public void refreshGameBackground(int[][] board) {
        gameBoardView.refreshGameBackground(board);
    }

    public void setGameSpeed(Duration duration) {
        if (gameLoop != null) {
            gameLoop.updateInterval(duration);
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
        if (isPause.getValue() == Boolean.FALSE) {
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
    }

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

    // Wire GameOverPanel button callbacks to controller behaviors
    private void wireGameOverButtons() {
        if (gameOverPanel == null) return;
        gameOverPanel.setOnRestart(() -> {
            // Restart the game
            newGame(null);
        });
        gameOverPanel.setOnMainMenu(() -> {
            // Invoke externally provided callback to return to main menu if set
            if (onReturnToMainMenu != null) {
                onReturnToMainMenu.run();
            }
        });

        // Also wire our new overlay buttons (if present) to the same actions
        if (gameOverRestartButton != null) {
            gameOverRestartButton.setOnAction(e -> newGame(null));
        }
        if (gameOverMainMenuButton != null) {
            gameOverMainMenuButton.setOnAction(e -> { if (onReturnToMainMenu != null) onReturnToMainMenu.run(); });
        }
    }

    public void gameOver() {
        gameLoop.stop();
        stopTimer();
        cancelResumeCountdown();
        stopDangerFlashTimer();
        stopDangerBoostTimer();
        stopDangerControlTimer();
        hidePauseOverlay();
        if (gameOverOverlay != null) {
            gameOverOverlay.setVisible(true);
            // Set final score for overlay
            if (gameOverScoreLabel != null && gameLoop != null) {
                // The score is set earlier by the caller in the pipeline, but also ensure to set to current game's score
            }
        } else {
            gameOverPanel.setVisible(true);
        }
        isGameOver.setValue(Boolean.TRUE);
    }

    public void setOnReturnToMainMenu(Runnable r) {
        this.onReturnToMainMenu = r;
    }

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

    public void pauseGame(ActionEvent actionEvent) {
        togglePause();
    }

    private void togglePause() {
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

    private void beginResumeCountdown() {
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

    private void showCountdown(String text) {
        NotificationPanel panel = new NotificationPanel(text);
        centerOverlay(panel);
        groupNotification.getChildren().add(panel);
        panel.showScore(groupNotification.getChildren());
    }

    private void centerOverlay(NotificationPanel panel) {
        double areaWidth = gamePanel.getBoundsInParent().getWidth();
        double areaHeight = gamePanel.getBoundsInParent().getHeight();
        double baseX = gamePanel.getLayoutX();
        double baseY = gamePanel.getLayoutY();
        double centeredX = baseX + Math.max(0, (areaWidth - panel.getMinWidth()) / 2);
        double centeredY = baseY + Math.max(0, (areaHeight - panel.getMinHeight()) / 2);
        panel.setLayoutX(centeredX);
        panel.setLayoutY(centeredY);
    }

    private void cancelResumeCountdown() {
        if (resumeCountdown != null) {
            resumeCountdown.stop();
            resumeCountdown = null;
        }
    }

    /**
     * Allows an external caller to set the current game mode (for example, for speed adjustments).
     * Recreates the underlying game loop with the new interval.
     */
    public void setGameMode(GameMode mode) {
        if (mode == null) {
            return;
        }
        if (gameLoop != null) {
            gameLoop.stop();
        }
        gameLoop = new GameLoop(Duration.millis(mode.getDropIntervalMs()), () -> moveDown(new MoveAction(ActionType.DOWN, ActionSource.THREAD)));
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
        if (!groupNotification.getChildren().contains(dangerFlashOverlay)) {
            groupNotification.getChildren().add(dangerFlashOverlay);
        }
        scheduleNextDangerFlash();
    }

    private void scheduleNextDangerFlash() {
        // Stop any existing one-shot timer first
        if (dangerFlashTimer != null) {
            dangerFlashTimer.stop();
            dangerFlashTimer = null;
        }
        double delay = DANGER_FLASH_MIN_SEC + dangerRandom.nextDouble() * (DANGER_FLASH_MAX_SEC - DANGER_FLASH_MIN_SEC);
        dangerFlashTimer = new Timeline(new KeyFrame(Duration.seconds(delay), e -> {
            // Always flash when the timer fires (randomness comes from scheduling interval)
            flashDangerOverlay();
            // reschedule next flash
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
        if (dangerFlashOverlay != null && groupNotification.getChildren().contains(dangerFlashOverlay)) {
            groupNotification.getChildren().remove(dangerFlashOverlay);
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
            // don't apply boost if paused or game over
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
        // Apply boost
        gameLoop.updateInterval(Duration.millis(boostedMs));
        // Schedule revert
        if (dangerBoostRevertTimer != null) {
            dangerBoostRevertTimer.stop();
            dangerBoostRevertTimer = null;
        }
        // Randomize duration slightly for variety (±0.5s)
        double duration = DANGER_BOOST_DURATION_SEC + (dangerRandom.nextDouble() - 0.5) * 1.0; // range [1.3 .. 2.3]
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
        // Revert to base interval if possible
        gameLoop.updateInterval(Duration.millis(currentBaseIntervalMs));
        if (dangerBoostRevertTimer != null) {
            dangerBoostRevertTimer.stop();
            dangerBoostRevertTimer = null;
        }
        // schedule next random boost
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
            // if boost active, revert now
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
            // Before the actual flip, show a warning and then activate flip briefly
            if (!isPause.getValue() && !isGameOver.getValue()) {
                showControlWarning("Controls Flipped!");
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
            // already flipped; schedule next
            scheduleNextDangerControl();
            return;
        }
        controlsFlipped = true;
        // schedule revert
        if (dangerControlRevertTimer != null) {
            dangerControlRevertTimer.stop();
            dangerControlRevertTimer = null;
        }
        double duration = DANGER_CONTROL_DURATION_SEC + (dangerRandom.nextDouble() - 0.5) * 1.5; // randomize a bit
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
        // schedule next
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
        javafx.scene.control.Label bubble = new javafx.scene.control.Label(message);
        bubble.getStyleClass().add("control-warning");
        bubble.setMinWidth(160);
        bubble.setMinHeight(36);
        // position near top center of gamePanel
        double areaWidth = gamePanel.getBoundsInParent().getWidth();
        double baseX = gamePanel.getLayoutX();
        double baseY = gamePanel.getLayoutY();
        double centeredX = baseX + Math.max(0, (areaWidth - bubble.getMinWidth()) / 2);
        double y = baseY + 18;
        bubble.setLayoutX(centeredX);
        bubble.setLayoutY(y);
        groupNotification.getChildren().add(bubble);
        FadeTransition ft = new FadeTransition(Duration.millis(650), bubble);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        ft.setDelay(Duration.millis(700));
        ft.setOnFinished(e -> groupNotification.getChildren().remove(bubble));
        ft.play();
    }

    private void flashDangerOverlay() {
        if (dangerFlashOverlay == null) {
            return;
        }
        // Fade in quickly
        FadeTransition in = new FadeTransition(Duration.millis(120), dangerFlashOverlay);
        in.setFromValue(0);
        in.setToValue(0.95);
        in.setCycleCount(1);
        in.setOnFinished(e -> {
            // Stay visible slightly longer before fading out
            PauseTransition hold = new PauseTransition(Duration.millis(400));
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
