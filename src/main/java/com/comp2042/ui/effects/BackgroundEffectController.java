package com.comp2042.ui.effects;

import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

/**
 * BackgroundEffectController manages all background-only visual effects.
 * This class is responsible for:
 * - Initializing background visual effects (neon grid, etc.)
 * - Managing the lifecycle of background animations
 * - Rendering background effects behind all other UI elements
 * 
 * This controller has no game logic or layout control - it only handles pure background visual effects.
 */
public class BackgroundEffectController {
    
    private final Pane rootPane;
    private NeonGridBackground neonGridBackground;
    
    /**
     * Constructor for BackgroundEffectController.
     * 
     * @param rootPane The root pane where background effects will be rendered
     */
    /**
     * Constructs a new BackgroundEffectController.
     * <p>
     * Initializes the controller with the root pane where background effects will be rendered.
     * </p>
     * 
     * @param rootPane The root pane of the scene.
     */
    public BackgroundEffectController(Pane rootPane) {
        this.rootPane = rootPane;
    }
    
    /**
     * Initializes all background visual effects.
     * <p>
     * Sets up and starts the neon grid background animation.
     * This method should be called during the application's UI initialization phase.
     * </p>
     */
    public void initialize() {
        initNeonGridBackground();
    }
    
    /**
     * Initializes the animated 80s-style neon grid background.
     * <p>
     * Creates the NeonGridBackground component, binds its size to the root pane,
     * inserts it at the bottom of the view stack (index 0) so it appears behind other elements,
     * and starts the animation loop.
     * </p>
     */
    private void initNeonGridBackground() {
        if (rootPane == null) {
            return;
        }
        
        neonGridBackground = new NeonGridBackground();
        
        // Bind the background size to the root pane
        neonGridBackground.prefWidthProperty().bind(rootPane.widthProperty());
        neonGridBackground.prefHeightProperty().bind(rootPane.heightProperty());
        
        // Insert the background at position 0 so it's behind everything else
        if (rootPane instanceof StackPane) {
            ((StackPane) rootPane).getChildren().add(0, neonGridBackground);
        }
        
        // Start the animation
        neonGridBackground.start();
    }
    
    /**
     * Gets the neon grid background instance.
     * 
     * @return The NeonGridBackground instance, or null if not initialized.
     */
    public NeonGridBackground getNeonGridBackground() {
        return neonGridBackground;
    }
    
    /**
     * Stops all background animations.
     * <p>
     * Halts the animation loop of the neon grid background.
     * This should be called when the controller is being disposed or the application is closing.
     * </p>
     */
    public void stop() {
        if (neonGridBackground != null) {
            neonGridBackground.stop();
        }
    }
}
