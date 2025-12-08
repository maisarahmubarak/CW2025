package com.comp2042.ui.state;

import com.comp2042.logic.game.GameLoop;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.Random;

/**
 * Manages all Danger Mode effects: flash overlay, speed boost, and control flip.
 */
public class DangerModeController {
    
    private final GridPane gamePanel;
    private final AnchorPane groupNotification;
    private final StackPane boardStack;
    private final GameStateManager stateManager;
    
    private GameLoop gameLoop;
    private double currentBaseIntervalMs = 400.0;
    
    // Flash effect
    private Timeline dangerFlashTimer;
    private Rectangle dangerFlashOverlay;
    private final Random dangerRandom = new Random();
    private static final double DANGER_FLASH_MIN_SEC = 4.0;
    private static final double DANGER_FLASH_MAX_SEC = 7.0;
    
    // Speed boost effect
    private Timeline dangerBoostTimer;
    private Timeline dangerBoostRevertTimer;
    private boolean dangerBoostActive = false;
    private static final double DANGER_BOOST_MIN_SEC = 6.0;
    private static final double DANGER_BOOST_MAX_SEC = 14.0;
    private static final double DANGER_BOOST_DURATION_SEC = 1.8;
    private static final double DANGER_BOOST_MIN_FACTOR = 0.10;
    private static final double DANGER_BOOST_MAX_FACTOR = 0.28;
    
    // Control flip effect
    private Timeline dangerControlTimer;
    private Timeline dangerControlActivateTimer;
    private Timeline dangerControlRevertTimer;
    private boolean controlsFlipped = false;
    private static final double DANGER_CONTROL_MIN_SEC = 7.0;
    private static final double DANGER_CONTROL_MAX_SEC = 18.0;
    private static final double DANGER_CONTROL_DURATION_SEC = 3.5;
    
    /**
     * Constructor for DangerModeController.
     */
    /**
     * Constructs a new DangerModeController.
     * <p>
     * Initializes the controller with the necessary UI components and state manager.
     * </p>
     * 
     * @param gamePanel The main game grid pane.
     * @param groupNotification The overlay notification pane.
     * @param boardStack The stack pane containing the board (used for flash overlay).
     * @param stateManager The game state manager.
     */
    public DangerModeController(GridPane gamePanel, AnchorPane groupNotification, 
                                StackPane boardStack, GameStateManager stateManager) {
        this.gamePanel = gamePanel;
        this.groupNotification = groupNotification;
        this.boardStack = boardStack;
        this.stateManager = stateManager;
    }
    
    /**
     * Sets the game loop for speed manipulation.
     * <p>
     * The game loop is required to adjust the drop interval during speed boost events.
     * </p>
     *
     * @param gameLoop The GameLoop instance.
     */
    public void setGameLoop(GameLoop gameLoop) {
        this.gameLoop = gameLoop;
    }
    
    /**
     * Sets the base interval for speed calculations.
     * <p>
     * This value is used as the reference for calculating the boosted speed and for restoring
     * the normal speed after a boost event ends.
     * </p>
     *
     * @param intervalMs The base drop interval in milliseconds.
     */
    public void setBaseInterval(double intervalMs) {
        this.currentBaseIntervalMs = intervalMs;
    }
    
    /**
     * Starts all danger mode effects.
     * <p>
     * Initiates the timers for random flash events, speed boosts, and control flips.
     * </p>
     */
    public void startDangerMode() {
        startDangerFlashTimer();
        startDangerBoostTimer();
        startDangerControlTimer();
    }
    
    /**
     * Stops all danger mode effects.
     * <p>
     * Cancels all active timers and reverts any active effects (e.g., restores normal speed,
     * unflipps controls, removes overlays).
     * </p>
     */
    public void stopDangerMode() {
        stopDangerFlashTimer();
        stopDangerBoostTimer();
        stopDangerControlTimer();
    }
    
    /**
     * Checks if controls are currently flipped.
     * 
     * @return true if controls are flipped (left is right, right is left), false otherwise.
     */
    public boolean isControlsFlipped() {
        return controlsFlipped;
    }
    
    // Flash effect methods
    
    /**
     * Starts the timer for the danger flash effect.
     */
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

    /**
     * Schedules the next danger flash event.
     */
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

    /**
     * Stops the danger flash timer and removes the overlay.
     */
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

    /**
     * Triggers the visual flash effect.
     */
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
    
    // Speed boost methods
    
    /**
     * Starts the timer for the speed boost effect.
     */
    private void startDangerBoostTimer() {
        if (dangerBoostTimer != null) {
            return;
        }
        scheduleNextDangerBoost();
    }

    /**
     * Schedules the next speed boost event.
     */
    private void scheduleNextDangerBoost() {
        if (dangerBoostTimer != null) {
            dangerBoostTimer.stop();
            dangerBoostTimer = null;
        }
        double delay = DANGER_BOOST_MIN_SEC + dangerRandom.nextDouble() * (DANGER_BOOST_MAX_SEC - DANGER_BOOST_MIN_SEC);
        dangerBoostTimer = new Timeline(new KeyFrame(Duration.seconds(delay), e -> {
            if (stateManager.isPaused() || stateManager.isGameOver()) {
                scheduleNextDangerBoost();
                return;
            }
            applyDangerBoost();
        }));
        dangerBoostTimer.setCycleCount(1);
        dangerBoostTimer.play();
    }

    /**
     * Applies the speed boost effect.
     */
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

    /**
     * Reverts the speed boost effect, restoring normal speed.
     */
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

    /**
     * Stops the speed boost timer and reverts any active boost.
     */
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
    
    // Control flip methods
    
    /**
     * Starts the timer for the control flip effect.
     */
    private void startDangerControlTimer() {
        if (dangerControlTimer != null) {
            return;
        }
        scheduleNextDangerControl();
    }

    /**
     * Schedules the next control flip event.
     */
    private void scheduleNextDangerControl() {
        if (dangerControlTimer != null) {
            dangerControlTimer.stop();
            dangerControlTimer = null;
        }
        double delay = DANGER_CONTROL_MIN_SEC + dangerRandom.nextDouble() * (DANGER_CONTROL_MAX_SEC - DANGER_CONTROL_MIN_SEC);
        dangerControlTimer = new Timeline(new KeyFrame(Duration.seconds(delay), e -> {
            if (stateManager.isPaused() || stateManager.isGameOver()) {
                scheduleNextDangerControl();
                return;
            }
            if (!stateManager.isPaused() && !stateManager.isGameOver()) {
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

    /**
     * Applies the control flip effect.
     */
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

    /**
     * Reverts the control flip effect.
     */
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

    /**
     * Stops the control flip timer and reverts any active flip.
     */
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

    /**
     * Shows a warning message for control changes.
     */
    private void showControlWarning(String message) {
        if (stateManager.isPaused() || stateManager.isGameOver()) return;
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
}
