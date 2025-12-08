package com.comp2042.ui.menu;

import com.comp2042.logic.game.GameSettings;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.layout.Pane;

/**
 * Controller for the settings screen.
 * Manages application settings such as volume and brightness.
 */
public class SettingsController {

    @FXML
    private Slider brightnessSlider;

    @FXML
    private Slider volumeSlider;

    private Runnable onBack;
    private Pane parentOverlay;

    @FXML
    public void initialize() {
        // Bind sliders to global settings
        brightnessSlider.setValue(GameSettings.getBrightness());
        volumeSlider.setValue(GameSettings.getVolume());

        brightnessSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            GameSettings.setBrightness(newVal.doubleValue());
            updateBrightnessEffect();
        });

        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            GameSettings.setVolume(newVal.doubleValue());
            System.out.println("Volume set to: " + newVal.intValue() + "%");
        });
    }

    public void setOnBack(Runnable onBack) {
        this.onBack = onBack;
    }
    
    public void setParentOverlay(Pane parentOverlay) {
        this.parentOverlay = parentOverlay;
        // Apply initial brightness when panel opens
        updateBrightnessEffect(); 
    }

    @FXML
    private void onBack() {
        if (onBack != null) {
            onBack.run();
        }
    }

    private void updateBrightnessEffect() {
        if (parentOverlay != null && parentOverlay.getScene() != null) {
             // Map slider [0, 1] to ColorAdjust [-1, 0]
             // 1.0 -> 0.0 (Normal)
             // 0.0 -> -1.0 (Black)
             double sliderVal = GameSettings.getBrightness();
             double colorAdjustVal = sliderVal - 1.0; 
             
             ColorAdjust adjust = new ColorAdjust();
             adjust.setBrightness(colorAdjustVal);
             parentOverlay.getScene().getRoot().setEffect(adjust);
        }
    }
}
