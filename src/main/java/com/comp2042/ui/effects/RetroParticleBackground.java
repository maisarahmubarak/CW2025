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
 * Creates an animated retro particle background with small glowing pixel-like squares
 * that drift upward or downward.
 * <p>
 * This component is designed to provide a visually interesting but non-distracting
 * background for menus or game screens. It features:
 * <ul>
 *   <li>Randomly generated square particles (pixels) of varying sizes.</li>
 *   <li>Neon color palette (Cyan, Magenta, Yellow, etc.) with glow effects.</li>
 *   <li>Vertical drift with slight horizontal movement for an organic feel.</li>
 *   <li>Fade-in and fade-out transitions for smooth appearance/disappearance.</li>
 * </ul>
 * The background manages its own particle lifecycle, spawning new particles as old ones
 * drift off-screen or fade out.
 * </p>
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

    /**
     * Constructs a new RetroParticleBackground.
     * <p>
     * Initializes the pane as transparent and non-interactive (mouse transparent).
     * Sets up listeners to spawn particles once the component has a valid size.
     * </p>
     */
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
     * <p>
     * Sets the running state to true and ensures the initial batch of particles is spawned.
     * </p>
     */
    public void start() {
        running = true;
        ensureParticleCount();
    }

    /**
     * Stops the particle animation and clears all particles.
     * <p>
     * Sets the running state to false, stops any active timelines, and removes all
     * particle nodes from the scene graph.
     * </p>
     */
    public void stop() {
        running = false;
        if (spawnTimeline != null) {
            spawnTimeline.stop();
        }
        getChildren().clear();
    }

    /**
     * Ensures that the background has the target number of particles.
     * <p>
     * If the current particle count is less than {@link #PARTICLE_COUNT}, new particles
     * are spawned until the limit is reached.
     * </p>
     */
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

    /**
     * Spawns a single particle with random properties and animation.
     * <p>
     * The particle is a small rectangle with a random neon color and glow effect.
     * Its movement is animated using {@link TranslateTransition} and {@link FadeTransition}.
     * When the animation finishes, the particle is removed and a new one is spawned
     * to maintain the particle count.
     * </p>
     *
     * @param randomStartY If true, the particle starts at a random Y position within the bounds
     *                     (used for initial population). If false, it starts just outside the
     *                     top or bottom edge (used for continuous spawning).
     */
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
