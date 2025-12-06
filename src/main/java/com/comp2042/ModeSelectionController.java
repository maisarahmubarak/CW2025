package com.comp2042;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.util.function.Consumer;

public class ModeSelectionController {

    @FXML
    private Label modeDescription;

    @FXML
    private Button btnCancel;

    private GameMode selectedMode = GameMode.CLASSIC;

    private Pane parentOverlay;
    private Consumer<GameMode> onModeChosen;
    private Runnable onCancelHandler;

    @FXML
    private void initialize() {
        btnCancel.setOnAction(e -> onCancel());
        modeDescription.setText("Select a mode to start the game.");
    }

    @FXML
    private void onClassic() {
        selectMode(GameMode.CLASSIC);
    }

    @FXML
    private void onSpeed() {
        selectMode(GameMode.SPEED);
    }

    @FXML
    private void onDanger() {
        selectMode(GameMode.DANGER);
    }

    private void selectMode(GameMode mode) {
        this.selectedMode = mode;
        if (onModeChosen != null) {
            onModeChosen.accept(selectedMode);
        }
        closeDialog();
    }

    private void onCancel() {
        selectedMode = null;
        if (onCancelHandler != null) {
            onCancelHandler.run();
        }
        closeDialog();
    }

    private void closeDialog() {
        if (parentOverlay == null) {
            Stage stage = (Stage) btnCancel.getScene().getWindow();
            if (stage != null) {
                stage.close();
            }
        }
    }

    public GameMode getSelectedMode() {
        return selectedMode;
    }

    public void setInitialMode(GameMode mode) {
        if (mode != null) {
            this.selectedMode = mode;
        }
    }

    public void setParentOverlay(Pane overlay) {
        this.parentOverlay = overlay;
    }

    public void setOnModeChosen(Consumer<GameMode> handler) {
        this.onModeChosen = handler;
    }

    public void setOnCancel(Runnable handler) {
        this.onCancelHandler = handler;
    }
}