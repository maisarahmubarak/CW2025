package com.comp2042.ui.state;

import com.comp2042.ui.overlay.NotificationPanel;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

/**
 * Handles pause and resume functionality including countdown animations.
 */
public class GamePauseController {
    
    private final GridPane gamePanel;
    private final AnchorPane groupNotification;
    private final GameStateManager stateManager;
    
    private Timeline resumeCountdown;
    private Runnable onResumeComplete;
    
    /**
     * Constructor for GamePauseController.
     */
    /**
     * Constructs a new GamePauseController.
     * <p>
     * Initializes the controller with the necessary UI components and state manager.
     * </p>
     * 
     * @param gamePanel The main game grid pane.
     * @param groupNotification The overlay notification pane.
     * @param stateManager The game state manager.
     */
    public GamePauseController(GridPane gamePanel, AnchorPane groupNotification, GameStateManager stateManager) {
        this.gamePanel = gamePanel;
        this.groupNotification = groupNotification;
        this.stateManager = stateManager;
    }
    
    /**
     * Sets the callback to execute when the resume countdown completes.
     * <p>
     * This callback is typically used to resume the game loop and timer.
     * </p>
     *
     * @param callback The Runnable to execute.
     */
    public void setOnResumeComplete(Runnable callback) {
        this.onResumeComplete = callback;
    }
    
    /**
     * Begins the resume countdown (3, 2, 1).
     * <p>
     * Starts a timeline that displays a countdown notification every second.
     * When the countdown finishes, the game is unpaused and the onResumeComplete callback is executed.
     * </p>
     */
    public void beginResumeCountdown() {
        final int[] remaining = {3};
        resumeCountdown = new Timeline(
                new KeyFrame(Duration.ZERO, e -> showCountdown(String.valueOf(remaining[0]--))),
                new KeyFrame(Duration.seconds(1))
        );
        resumeCountdown.setCycleCount(3);
        resumeCountdown.setOnFinished(e -> {
            stateManager.setPaused(false);
            resumeCountdown = null;
            if (onResumeComplete != null) {
                onResumeComplete.run();
            }
        });
        resumeCountdown.playFromStart();
    }
    
    /**
     * Cancels the resume countdown if it is currently active.
     * <p>
     * Stops the timeline and clears the reference.
     * </p>
     */
    public void cancelResumeCountdown() {
        if (resumeCountdown != null) {
            resumeCountdown.stop();
            resumeCountdown = null;
        }
    }
    
    /**
     * Checks if a resume countdown is currently active.
     * 
     * @return true if the countdown is running, false otherwise.
     */
    public boolean isCountdownActive() {
        return resumeCountdown != null;
    }
    
    /**
     * Shows a countdown notification with the specified text.
     * <p>
     * Creates a NotificationPanel, centers it on the game board, adds it to the overlay,
     * and triggers its animation.
     * </p>
     *
     * @param text The text to display (e.g., "3", "2", "1").
     */
    private void showCountdown(String text) {
        NotificationPanel panel = new NotificationPanel(text);
        centerOverlay(panel);
        groupNotification.getChildren().add(panel);
        panel.animateCountdown(groupNotification.getChildren());
    }
    
    /**
     * Centers an overlay panel on the game board.
     * <p>
     * Calculates the center coordinates of the game panel relative to the notification pane
     * and positions the panel accordingly.
     * </p>
     *
     * @param panel The NotificationPanel to center.
     */
    private void centerOverlay(NotificationPanel panel) {
        if (gamePanel == null || groupNotification == null) return;

        Bounds boundsInScene = gamePanel.localToScene(gamePanel.getBoundsInLocal());
        double centerX = boundsInScene.getMinX() + boundsInScene.getWidth() / 2;
        double centerY = boundsInScene.getMinY() + boundsInScene.getHeight() / 2;

        Point2D centerInParent = groupNotification.sceneToLocal(centerX, centerY);

        panel.setLayoutX(centerInParent.getX() - panel.getMinWidth() / 2);
        panel.setLayoutY(centerInParent.getY() - panel.getMinHeight() / 2);
    }
}
