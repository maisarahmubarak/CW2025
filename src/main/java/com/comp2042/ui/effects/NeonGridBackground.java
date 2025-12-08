package com.comp2042.ui.effects;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.Glow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

/**
 * Creates an animated 80s-style neon grid background using a Canvas.
 * <p>
 * This class renders a "Synthwave" aesthetic background featuring:
 * <ul>
 *   <li>A scrolling perspective grid (the "floor") moving towards the viewer.</li>
 *   <li>A retro-style sun on the horizon with horizontal stripe cutouts.</li>
 *   <li>A starry sky with gradient background.</li>
 *   <li>Neon colors (Cyan/Magenta) with Glow and Bloom effects.</li>
 * </ul>
 * The animation is driven by an {@link AnimationTimer} which updates the grid offset
 * to create the illusion of forward movement.
 *
 * @author Maisarah
 * @version 1.0
 */
public class NeonGridBackground extends Pane {

    private static final double GRID_SPACING = 40.0;
    private static final double LINE_WIDTH = 1.5;
    private static final double SCROLL_SPEED = 0.3; // pixels per frame (slow scrolling)
    private static final double HORIZON_Y_RATIO = 0.35; // horizon line position (35% from top)

    private final Canvas canvas;
    private final AnimationTimer animationTimer;
    private double verticalOffset = 0;

    // Neon colors for 80s aesthetic
    private final Color neonCyan = Color.rgb(0, 255, 255, 0.7);
    private final Color neonMagenta = Color.rgb(255, 0, 255, 0.5);
    private final Color horizonGlow = Color.rgb(255, 100, 200, 0.8);

    /**
     * Constructs a new NeonGridBackground.
     * <p>
     * Initializes the canvas, binds its size to the pane, applies neon glow effects,
     * and sets up the animation loop for the scrolling grid effect.
     * </p>
     */
    public NeonGridBackground() {
        canvas = new Canvas();
        getChildren().add(canvas);

        // Bind canvas size to pane size
        canvas.widthProperty().bind(widthProperty());
        canvas.heightProperty().bind(heightProperty());

        // Add glow effect for neon look
        Glow glow = new Glow(0.6);
        Bloom bloom = new Bloom(0.3);
        glow.setInput(bloom);
        canvas.setEffect(glow);

        // Redraw when size changes
        widthProperty().addListener((obs, oldVal, newVal) -> draw());
        heightProperty().addListener((obs, oldVal, newVal) -> draw());

        // Animation timer for smooth scrolling
        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                verticalOffset += SCROLL_SPEED;
                if (verticalOffset >= GRID_SPACING) {
                    verticalOffset -= GRID_SPACING;
                }
                draw();
            }
        };
    }

    /**
     * Starts the background animation.
     * <p>
     * This method activates the {@link AnimationTimer}, causing the grid to scroll
     * and the scene to be redrawn on every frame.
     * </p>
     */
    public void start() {
        animationTimer.start();
    }

    /**
     * Stops the background animation.
     * <p>
     * This method halts the {@link AnimationTimer}, freezing the background in its current state.
     * </p>
     */
    public void stop() {
        animationTimer.stop();
    }

    /**
     * Main drawing method that orchestrates the rendering of the scene.
     * <p>
     * Clears the canvas and draws the background gradient, perspective grid,
     * sky lines, and the sun/horizon elements in order.
     * </p>
     */
    private void draw() {
        double width = canvas.getWidth();
        double height = canvas.getHeight();

        if (width <= 0 || height <= 0) {
            return;
        }

        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Clear the canvas with a dark gradient background
        gc.clearRect(0, 0, width, height);
        drawBackground(gc, width, height);

        // Draw the perspective grid (floor)
        drawPerspectiveGrid(gc, width, height);

        // Draw horizontal lines above horizon for sky effect
        drawSkyLines(gc, width, height);

        // Draw the sun/horizon glow
        drawHorizonSun(gc, width, height);
    }

    /**
     * Draws the background gradient representing the deep space/sky.
     *
     * @param gc     The graphics context to draw on.
     * @param width  The width of the canvas.
     * @param height The height of the canvas.
     */
    private void drawBackground(GraphicsContext gc, double width, double height) {
        // Dark gradient from top to bottom
        LinearGradient bgGradient = new LinearGradient(
                0, 0, 0, height,
                false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(2, 6, 23)),
                new Stop(0.3, Color.rgb(15, 10, 40)),
                new Stop(0.5, Color.rgb(30, 10, 50)),
                new Stop(1.0, Color.rgb(10, 5, 30))
        );
        gc.setFill(bgGradient);
        gc.fillRect(0, 0, width, height);
    }

    /**
     * Draws the perspective grid (the "floor") with scrolling effect.
     * <p>
     * Renders vertical lines converging to a vanishing point and horizontal lines
     * that move downwards to create the illusion of forward motion.
     * </p>
     *
     * @param gc     The graphics context to draw on.
     * @param width  The width of the canvas.
     * @param height The height of the canvas.
     */
    private void drawPerspectiveGrid(GraphicsContext gc, double width, double height) {
        double horizonY = height * HORIZON_Y_RATIO;
        double floorHeight = height - horizonY;

        // Vanishing point at center horizon
        double vanishX = width / 2;

        gc.setStroke(neonCyan);
        gc.setLineWidth(LINE_WIDTH);

        // Draw vertical perspective lines from horizon to bottom
        int numVerticalLines = 20;
        double bottomSpread = width * 1.5; // lines spread wider at bottom

        for (int i = -numVerticalLines / 2; i <= numVerticalLines / 2; i++) {
            double bottomX = vanishX + (i * bottomSpread / numVerticalLines);
            gc.setStroke(Color.rgb(0, 255, 255, 0.4 + 0.3 * (1 - Math.abs(i) / (double) (numVerticalLines / 2))));
            gc.strokeLine(vanishX, horizonY, bottomX, height);
        }

        // Draw horizontal perspective lines with scrolling effect
        gc.setStroke(neonMagenta);
        int numHorizontalLines = 15;

        for (int i = 0; i <= numHorizontalLines; i++) {
            // Calculate Y position with perspective (lines get closer together near horizon)
            double progress = (i + verticalOffset / GRID_SPACING) / numHorizontalLines;
            if (progress > 1.0) progress -= 1.0;

            // Exponential spacing for perspective effect
            double perspectiveY = horizonY + floorHeight * Math.pow(progress, 1.8);

            if (perspectiveY >= horizonY && perspectiveY <= height) {
                // Fade lines near horizon
                double alpha = 0.3 + 0.5 * progress;
                gc.setStroke(Color.rgb(255, 0, 255, alpha));
                gc.setLineWidth(LINE_WIDTH * (0.5 + 0.5 * progress));
                gc.strokeLine(0, perspectiveY, width, perspectiveY);
            }
        }
    }

    /**
     * Draws faint horizontal lines in the sky area to add texture.
     *
     * @param gc     The graphics context to draw on.
     * @param width  The width of the canvas.
     * @param height The height of the canvas.
     */
    private void drawSkyLines(GraphicsContext gc, double width, double height) {
        double horizonY = height * HORIZON_Y_RATIO;

        // Draw faint horizontal lines in the sky area
        gc.setLineWidth(0.5);
        int numSkyLines = 8;

        for (int i = 1; i <= numSkyLines; i++) {
            double y = horizonY * (1 - (double) i / (numSkyLines + 1));
            double alpha = 0.1 + 0.1 * (1 - (double) i / numSkyLines);
            gc.setStroke(Color.rgb(100, 50, 150, alpha));
            gc.strokeLine(0, y, width, y);
        }
    }

    /**
     * Draws the retro sun and the horizon glow.
     * <p>
     * The sun is rendered with a gradient and horizontal stripe cutouts, typical of
     * 80s synthwave aesthetics.
     * </p>
     *
     * @param gc     The graphics context to draw on.
     * @param width  The width of the canvas.
     * @param height The height of the canvas.
     */
    private void drawHorizonSun(GraphicsContext gc, double width, double height) {
        double horizonY = height * HORIZON_Y_RATIO;
        double sunRadius = Math.min(width, height) * 0.15;
        double sunCenterX = width / 2;
        double sunCenterY = horizonY;

        // Draw sun glow gradient
        for (int i = 5; i >= 0; i--) {
            double radius = sunRadius * (1 + i * 0.3);
            double alpha = 0.15 - i * 0.02;
            gc.setFill(Color.rgb(255, 100, 150, Math.max(0.02, alpha)));
            gc.fillOval(sunCenterX - radius, sunCenterY - radius * 0.5, radius * 2, radius);
        }

        // Draw the sun with horizontal stripes (80s style)
        gc.save();
        gc.beginPath();
        gc.arc(sunCenterX, sunCenterY, sunRadius, sunRadius * 0.5, 0, 360);
        gc.closePath();
        gc.clip();

        // Sun gradient
        LinearGradient sunGradient = new LinearGradient(
                0, sunCenterY - sunRadius * 0.5, 0, sunCenterY + sunRadius * 0.5,
                false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(255, 255, 100)),
                new Stop(0.5, Color.rgb(255, 150, 50)),
                new Stop(1.0, Color.rgb(255, 50, 100))
        );
        gc.setFill(sunGradient);
        gc.fillOval(sunCenterX - sunRadius, sunCenterY - sunRadius * 0.5, sunRadius * 2, sunRadius);

        // Draw horizontal stripes across the sun
        gc.setFill(Color.rgb(2, 6, 23, 0.9));
        double stripeHeight = sunRadius * 0.08;
        for (int i = 1; i <= 5; i++) {
            double stripeY = sunCenterY - sunRadius * 0.3 + i * sunRadius * 0.12;
            gc.fillRect(sunCenterX - sunRadius, stripeY, sunRadius * 2, stripeHeight);
        }

        gc.restore();

        // Horizon line glow
        gc.setStroke(horizonGlow);
        gc.setLineWidth(2);
        gc.strokeLine(0, horizonY, width, horizonY);
    }
}
