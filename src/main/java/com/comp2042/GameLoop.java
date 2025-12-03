package com.comp2042;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

/**
 * Encapsulates the recurring DOWN movement tick that GuiController was previously
 * responsible for and keeps the UI loop behavior localized.
 */
public class GameLoop {

    private final Timeline timeline;

    public GameLoop(Duration interval, Runnable tick) {
        timeline = new Timeline(new KeyFrame(interval, ae -> tick.run()));
        timeline.setCycleCount(Timeline.INDEFINITE);
    }

    public void play() {
        timeline.play();
    }

    public void stop() {
        timeline.stop();
    }
}
