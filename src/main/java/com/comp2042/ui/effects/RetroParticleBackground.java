package com.comp2042.ui.effects;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.effect.Glow;
import javafx.util.Duration;

import java.util.Random;

/**
 * Creates an animated retro particle background with small glowing pixel-like circles
 * that drift upward or downward. Perfect for a main menu aesthetic.
 */
public class RetroParticleBackground extends Pane {

    private static final int PARTICLE_COUNT = 150;
    private static final double MIN_PARTICLE_SIZE = 2.0;
    private static final double MAX_PARTICLE_SIZE = 5.0;
    private static final double MIN_DURATION_SEC = 8.0;
    private static final double MAX_DURATION_SEC = 16.0;

    private final Random random = new Random();
    private Timeline spawnTimeline;
    private boolean running = false;

    // Retro neon colors for particles
    private final Color[] particleColors = {
            Color.rgb(0, 255, 255, 0.8),    // Cyan
            Color.rgb(255, 0, 255, 0.8),    // Magenta
            Color.rgb(255, 255, 0, 0.7),    // Yellow
            Color.rgb(0, 255, 128, 0.7),    // Green
            Color.rgb(255, 100, 200, 0.7),  // Pink
            Color.rgb(128, 0, 255, 0.7),    // Purple
            Color.rgb(255, 128, 0, 0.6),    // Orange
    };

    public RetroParticleBackground() {
        // Make the pane transparent and non-interactive
        setPickOnBounds(false);
        setMouseTransparent(true);

        // Spawn particles when size is available
        widthProperty().addListener((obs, oldVal, newVal) -> {
            if (running && newVal.doubleValue() > 0) {
                ensureParticleCount();
            }
        });
        heightProperty().addListener((obs, oldVal, newVal) -> {
            if (running && newVal.doubleValue() > 0) {
                ensureParticleCount();
            }
        });
    }

    /**
     * Starts the particle animation.
     */
    public void start() {
        running = true;
        ensureParticleCount();
    }

    /**
     * Stops the particle animation and clears all particles.
     */
    public void stop() {
        running = false;
        if (spawnTimeline != null) {
            spawnTimeline.stop();
        }
        getChildren().clear();
    }

    private void ensureParticleCount() {
        double width = getWidth();
        double height = getHeight();

        if (width <= 0 || height <= 0) {
            return;
        }

        // Spawn initial particles spread across the screen
        int currentCount = getChildren().size();
        for (int i = currentCount; i < PARTICLE_COUNT; i++) {
            spawnParticle(true);
        }
    }

    private void spawnParticle(boolean randomStartY) {
        double width = getWidth();
        double height = getHeight();

        if (width <= 0 || height <= 0) {
            return;
        }

        // Create particle (Square for pixel look)
        double size = MIN_PARTICLE_SIZE + random.nextDouble() * (MAX_PARTICLE_SIZE - MIN_PARTICLE_SIZE);
        Rectangle particle = new Rectangle(size, size);

        // Random neon color
        Color color = particleColors[random.nextInt(particleColors.length)];
        particle.setFill(color);

        // Add glow effect for neon look
        Glow glow = new Glow(0.8);
        particle.setEffect(glow);

        // Random X position
        double startX = random.nextDouble() * width;
        particle.setX(startX);

        // Determine if particle moves up or down
        boolean movesUp = random.nextBoolean();

        // Set starting Y position
        double startY;
        if (randomStartY) {
            // For initial spawn, spread particles across the whole screen
            startY = random.nextDouble() * height;
        } else {
            // For continuous spawning, start from edge
            startY = movesUp ? height + size : -size;
        }
        particle.setY(startY);

        // Calculate travel distance and end position
        double endY = movesUp ? -size : height + size;
        double travelDistance = Math.abs(endY - startY);

        // Random duration based on distance
        double baseDuration = MIN_DURATION_SEC + random.nextDouble() * (MAX_DURATION_SEC - MIN_DURATION_SEC);
        double durationFactor = travelDistance / height;
        double duration = baseDuration * durationFactor;

        // Translate animation (vertical drift)
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(duration), particle);
        translateTransition.setByY(endY - startY);

        // Slight horizontal drift for more organic feel
        double horizontalDrift = (random.nextDouble() - 0.5) * 60;
        translateTransition.setByX(horizontalDrift);

        // Fade animation
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(duration * 0.1), particle);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(0.6 + random.nextDouble() * 0.4);

        FadeTransition fadeOut = new FadeTransition(Duration.seconds(duration * 0.2), particle);
        fadeOut.setDelay(Duration.seconds(duration * 0.7));
        fadeOut.setToValue(0);

        // Combine animations
        ParallelTransition animation = new ParallelTransition(translateTransition, fadeIn, fadeOut);

        // When animation finishes, remove particle and spawn new one
        animation.setOnFinished(event -> {
            getChildren().remove(particle);
            if (running) {
                spawnParticle(false);
            }
        });

        // Add particle and start animation
        getChildren().add(particle);
        animation.play();
    }
}
