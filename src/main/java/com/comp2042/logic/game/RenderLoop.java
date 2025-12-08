package com.comp2042.logic.game;

import javafx.animation.AnimationTimer;

/**
 * Manages the rendering loop using JavaFX's AnimationTimer.
 * <p>
 * Executes a task on every frame of the JavaFX application thread.
 * </p>
 */
public class RenderLoop {

    private final AnimationTimer timer;

    /**
     * Creates a new RenderLoop.
     *
     * @param frame the task to execute on each frame
     */
    public RenderLoop(Runnable frame) {
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                frame.run();
            }
        };
    }

    /**
     * Starts the rendering loop.
     */
    public void play() {
        timer.start();
    }

    /**
     * Stops the rendering loop.
     */
    public void stop() {
        timer.stop();
    }
}