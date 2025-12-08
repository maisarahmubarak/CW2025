package com.comp2042.input;

import com.comp2042.ui.GuiController;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;

/**
 * Keyboard input handler for game controls.
 * <p>
 * This class implements {@link EventHandler} for {@link KeyEvent}s and delegates the processing
 * of these events to the {@link GuiController}.
 * </p>
 *
 * @author Maisarah
 * @version 1.0
 */
public class GameKeyHandler implements EventHandler<KeyEvent> {

    private final GuiController controller;

    /**
     * Constructs a new GameKeyHandler.
     *
     * @param controller the {@link GuiController} to delegate key events to.
     */
    public GameKeyHandler(GuiController controller) {
        this.controller = controller;
    }

    /**
     * Handles the key event.
     *
     * @param event the {@link KeyEvent} that occurred.
     */
    @Override
    public void handle(KeyEvent event) {
        controller.handleKeyEvent(event);
    }
}
