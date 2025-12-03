package com.comp2042;

import javafx.animation.AnimationTimer;

public class RenderLoop {

    private final AnimationTimer timer;

    public RenderLoop(Runnable frame) {
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                frame.run();
            }
        };
    }

    public void play() {
        timer.start();
    }

    public void stop() {
        timer.stop();
    }
}
