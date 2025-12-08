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
    public GameTimerController(Label timerLabel) {
        this.timerLabel = timerLabel;
    }
    
    /**
     * Initializes the timer timeline.
     * Creates a timeline that increments elapsed seconds every second
     * and updates the timer label display.
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
     * The timer will begin counting seconds and updating the display.
     */
    public void startTimer() {
        if (timerTimeline != null) {
            timerTimeline.play();
        }
    }
    
    /**
     * Stops the timer.
     * The timer will pause at the current elapsed time.
     */
    public void stopTimer() {
        if (timerTimeline != null) {
            timerTimeline.stop();
        }
    }
    
    /**
     * Resets the timer.
     * Sets elapsed seconds back to zero and updates the display to 00:00.
     */
    public void resetTimer() {
        elapsedSeconds = 0;
        updateTimerLabel();
    }
    
    /**
     * Updates the timer label with the current elapsed time.
     * Formats the time as MM:SS (e.g., "03:45" for 3 minutes 45 seconds).
     */
    private void updateTimerLabel() {
        int minutes = elapsedSeconds / 60;
        int seconds = elapsedSeconds % 60;
        timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
    }
}
