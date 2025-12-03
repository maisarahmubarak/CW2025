package com.comp2042;

import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;

/**
 * Delegates KeyEvent handling to the GuiController's handleKeyEvent method.
 * This class holds no logic itself so behavior is unchanged; it simply
 * extracts the anonymous handler into a named class.
 */
public class GameKeyHandler implements EventHandler<KeyEvent> {

    private final GuiController controller;

    public GameKeyHandler(GuiController controller) {
        this.controller = controller;
    }

    @Override
    public void handle(KeyEvent event) {
        controller.handleKeyEvent(event);
    }
}
