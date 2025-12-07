package com.comp2042;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.util.Duration;

import java.util.Random;

/**
 * Utility to run one-time title reveal/flicker animation on a Label.
 */
public final class TitleRevealAnimator {

    private static final String PLAYED_PROPERTY = "titleRevealPlayed";
    private static final int DEFAULT_STEP_MS = 100;
    private static final int DEFAULT_FLICKER_OFFSET = 30; // ms before reveal
    private static final double DEFAULT_FLICKER_PROB = 0.25; // 25%

    private static final Random random = new Random();

    private TitleRevealAnimator() {
        // utility class
    }

    public static void startOnce(Label label) {
        startOnce(label, DEFAULT_STEP_MS, DEFAULT_FLICKER_OFFSET, DEFAULT_FLICKER_PROB);
    }

    public static void startOnce(Label label, int stepMs, int flickerOffsetMs, double flickerProb) {
        if (label == null) return;
        Object played = label.getProperties().get(PLAYED_PROPERTY);
        if (played instanceof Boolean && (Boolean) played) {
            return; // already played
        }
        String original = label.getText();
        if (original == null || original.isEmpty()) return;

        label.getProperties().put(PLAYED_PROPERTY, Boolean.TRUE);
        label.setText("");

        Timeline timeline = new Timeline();
        timeline.setCycleCount(1);
        for (int i = 1; i <= original.length(); i++) {
            final int len = i;
            double flickerMs = len * stepMs - flickerOffsetMs;
            if (flickerMs > 0) {
                timeline.getKeyFrames().add(new KeyFrame(Duration.millis(flickerMs), e -> {
                    if (random.nextDouble() < flickerProb) {
                        label.setText(generateFlickerFor(original, len));
                    }
                }));
            }
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(len * stepMs), e -> label.setText(original.substring(0, len))));
        }
        timeline.play();
    }

    private static String generateFlickerFor(String original, int len) {
        if (original == null || original.isEmpty()) return "";
        int prefixLen = Math.max(0, len - 1);
        String prefix = original.substring(0, prefixLen);
        // pick a random character from the original for the last char
        char randomChar = original.charAt(random.nextInt(original.length()));
        return prefix + randomChar;
    }
}
