package com.comp2042.ui.state;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.util.Duration;

/**
 * GameTimerController is solely responsible for managing the game timer:
 * - Creating and initializing the timer
 * - Starting, stopping, and resetting the timer
 * - Updating the timer display label
 * 
 * This controller handles all timer-related logic and state, keeping track
 * of elapsed time and formatting it for display in MM:SS format.
 */
public class GameTimerController {
    
    private Timeline timerTimeline;
    private int elapsedSeconds = 0;
    private final Label timerLabel;
    
    /**
     * Constructor for GameTimerController.
     * 
     * @param timerLabel The label to display the timer in MM:SS format
     */
    /**
     * Constructs a new GameTimerController.
     * <p>
     * Initializes the controller with the label used to display the timer.
     * </p>
     * 
     * @param timerLabel The label to display the timer in MM:SS format.
     */
    public GameTimerController(Label timerLabel) {
        this.timerLabel = timerLabel;
    }
    
    /**
     * Initializes the timer timeline.
     * <p>
     * Creates a timeline that increments the elapsed seconds counter every second
     * and updates the timer label display. The timeline is set to run indefinitely.
     * </p>
     */
    public void initializeTimer() {
        timerTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            elapsedSeconds++;
            updateTimerLabel();
        }));
        timerTimeline.setCycleCount(Timeline.INDEFINITE);
    }
    
    /**
     * Starts the timer.
     * <p>
     * Begins or resumes the timeline execution, causing the timer to count up.
     * </p>
     */
    public void startTimer() {
        if (timerTimeline != null) {
            timerTimeline.play();
        }
    }
    
    /**
     * Stops the timer.
     * <p>
     * Pauses the timeline execution. The elapsed time is preserved.
     * </p>
     */
    public void stopTimer() {
        if (timerTimeline != null) {
            timerTimeline.stop();
        }
    }
    
    /**
     * Resets the timer.
     * <p>
     * Sets the elapsed seconds counter back to zero and updates the display to "00:00".
     * </p>
     */
    public void resetTimer() {
        elapsedSeconds = 0;
        updateTimerLabel();
    }
    
    /**
     * Updates the timer label with the current elapsed time.
     * <p>
     * Formats the elapsed seconds into a "MM:SS" string (e.g., "03:45") and sets it on the label.
     * </p>
     */
    private void updateTimerLabel() {
        int minutes = elapsedSeconds / 60;
        int seconds = elapsedSeconds % 60;
        timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
    }
}
