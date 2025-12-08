package com.comp2042.ui.menu;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

/**
 * Controller for the tutorial screen.
 * Displays game instructions and handles navigation back to the main menu.
 */
public class TutorialController {

    private Runnable onBack;
    private Pane parentOverlay;

    /**
     * Sets the callback to be executed when the back button is pressed.
     *
     * @param onBack a {@link Runnable} to execute.
     */
    public void setOnBack(Runnable onBack) {
        this.onBack = onBack;
    }
    
    /**
     * Sets the parent overlay pane.
     *
     * @param parentOverlay the parent {@link Pane}.
     */
    public void setParentOverlay(Pane parentOverlay) {
        this.parentOverlay = parentOverlay;
    }

    @FXML
    private void onBack() {
        if (onBack != null) {
            onBack.run();
        }
    }
}
