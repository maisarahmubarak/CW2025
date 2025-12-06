package com.comp2042;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

public class TutorialController {

    private Runnable onBack;
    private Pane parentOverlay;

    public void setOnBack(Runnable onBack) {
        this.onBack = onBack;
    }
    
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
