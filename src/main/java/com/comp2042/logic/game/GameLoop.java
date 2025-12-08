package com.comp2042.logic.game;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

/**
 * Manages the game loop using a JavaFX Timeline.
 * Executes a specified task (tick) at regular intervals.
 *
 * @author Maisarah
 * @version 1.0
 */
public class GameLoop {

    private final Timeline timeline;
    private final Runnable tick;

    /**
     * Creates a new GameLoop.
     *
     * @param interval the duration between ticks
     * @param tick the task to execute on each tick
     */
    public GameLoop(Duration interval, Runnable tick) {
        this.tick = tick;
        timeline = new Timeline(new KeyFrame(interval, ae -> tick.run()));
        timeline.setCycleCount(Timeline.INDEFINITE);
    }

    /**
     * Starts the game loop.
     */
    public void play() {
        timeline.play();
    }

    /**
     * Stops the game loop.
     */
    public void stop() {
        timeline.stop();
    }

    /**
     * Checks if the game loop is currently running.
     *
     * @return true if running, false otherwise
     */
    public boolean isRunning() {
        return timeline.getStatus() == javafx.animation.Animation.Status.RUNNING;
    }

    /**
     * Updates the interval between ticks.
     * If the loop is running, it restarts with the new interval.
     *
     * @param interval the new duration between ticks
     */
    public void updateInterval(Duration interval) {
        boolean running = (timeline.getStatus() == javafx.animation.Animation.Status.RUNNING);
        timeline.stop();
        timeline.getKeyFrames().clear();
        timeline.getKeyFrames().add(new KeyFrame(interval, ae -> tick.run()));
        if (running) {
            timeline.play();
        }
    }
}
