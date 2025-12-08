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
    public GamePauseController(GridPane gamePanel, AnchorPane groupNotification, GameStateManager stateManager) {
        this.gamePanel = gamePanel;
        this.groupNotification = groupNotification;
        this.stateManager = stateManager;
    }
    
    /**
     * Sets the callback to execute when resume is complete.
     */
    public void setOnResumeComplete(Runnable callback) {
        this.onResumeComplete = callback;
    }
    
    /**
     * Begins the resume countdown (3, 2, 1).
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
     * Cancels the resume countdown if active.
     */
    public void cancelResumeCountdown() {
        if (resumeCountdown != null) {
            resumeCountdown.stop();
            resumeCountdown = null;
        }
    }
    
    /**
     * Checks if a countdown is currently active.
     */
    public boolean isCountdownActive() {
        return resumeCountdown != null;
    }
    
    /**
     * Shows the countdown notification.
     */
    private void showCountdown(String text) {
        NotificationPanel panel = new NotificationPanel(text);
        centerOverlay(panel);
        groupNotification.getChildren().add(panel);
        panel.animateCountdown(groupNotification.getChildren());
    }
    
    /**
     * Centers an overlay panel on the game board.
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
