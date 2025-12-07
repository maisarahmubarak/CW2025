package com.comp2042.logic.game;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class GameLoop {

    private final Timeline timeline;
    private final Runnable tick;

    public GameLoop(Duration interval, Runnable tick) {
        this.tick = tick;
        timeline = new Timeline(new KeyFrame(interval, ae -> tick.run()));
        timeline.setCycleCount(Timeline.INDEFINITE);
    }

    public void play() {
        timeline.play();
    }

    public void stop() {
        timeline.stop();
    }

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