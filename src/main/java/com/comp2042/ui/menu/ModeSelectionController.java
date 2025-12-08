package com.comp2042.ui.menu;

import com.comp2042.ui.effects.TitleRevealAnimator;
import com.comp2042.logic.game.GameMode;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.util.function.Consumer;

/**
 * Controller for the game mode selection screen.
 * <p>
 * This class manages the UI for selecting the game difficulty/mode. It allows the user to:
 * <ul>
 *   <li>Choose between Classic, Speed, and Danger modes.</li>
 *   <li>View a brief description of the selected mode.</li>
 *   <li>Cancel the selection and return to the main menu.</li>
 * </ul>
 * It supports being displayed as an overlay within the main menu scene.
 * </p>
 */
public class ModeSelectionController {

    @FXML
    private Label titleLabel;
    @FXML
    private Label modeDescription;

    @FXML
    private Button btnCancel;

    private GameMode selectedMode = GameMode.CLASSIC;

    private Pane parentOverlay;
    private Consumer<GameMode> onModeChosen;
    private Runnable onCancelHandler;

    /**
     * Initializes the controller class.
     * <p>
     * Sets up the cancel button action, clears the initial description, and triggers
     * the title reveal animation for the "Select Mode" label.
     * </p>
     */
    @FXML
    private void initialize() {
        btnCancel.setOnAction(e -> onCancel());
        // no default text is shown; the description will be set dynamically if needed
        modeDescription.setText("");
        // Play the title reveal animation when the dialog is shown
        javafx.application.Platform.runLater(() -> TitleRevealAnimator.startOnce(titleLabel));
    }

    /**
     * Handles the selection of "Classic" mode.
     */
    @FXML
    private void onClassic() {
        selectMode(GameMode.CLASSIC);
    }

    /**
     * Handles the selection of "Speed" mode.
     */
    @FXML
    private void onSpeed() {
        selectMode(GameMode.SPEED);
    }

    /**
     * Handles the selection of "Danger" mode.
     */
    @FXML
    private void onDanger() {
        selectMode(GameMode.DANGER);
    }

    /**
     * Processes the mode selection.
     * <p>
     * Updates the selected mode, triggers the selection callback if set, and closes the dialog.
     * </p>
     *
     * @param mode The {@link GameMode} chosen by the user.
     */
    private void selectMode(GameMode mode) {
        this.selectedMode = mode;
        if (onModeChosen != null) {
            onModeChosen.accept(selectedMode);
        }
        closeDialog();
    }

    /**
     * Handles the cancel action.
     * <p>
     * Clears the selected mode, triggers the cancel callback if set, and closes the dialog.
     * </p>
     */
    private void onCancel() {
        selectedMode = null;
        if (onCancelHandler != null) {
            onCancelHandler.run();
        }
        closeDialog();
    }

    /**
     * Closes the mode selection dialog.
     * <p>
     * If the dialog is displayed in a separate stage, it closes the stage.
     * If it is an overlay, the removal is handled by the parent controller via callbacks.
     * </p>
     */
    private void closeDialog() {
        if (parentOverlay == null) {
            Stage stage = (Stage) btnCancel.getScene().getWindow();
            if (stage != null) {
                stage.close();
            }
        }
    }

    /**
     * Gets the currently selected game mode.
     *
     * @return the selected {@link GameMode}, or null if cancelled.
     */
    public GameMode getSelectedMode() {
        return selectedMode;
    }

    /**
     * Sets the initial game mode to be selected.
     *
     * @param mode the {@link GameMode} to select initially.
     */
    public void setInitialMode(GameMode mode) {
        if (mode != null) {
            this.selectedMode = mode;
        }
    }

    /**
     * Sets the parent overlay pane.
     * <p>
     * This is used to determine if the controller is running as an overlay or a standalone window.
     * </p>
     *
     * @param overlay the parent {@link Pane}.
     */
    public void setParentOverlay(Pane overlay) {
        this.parentOverlay = overlay;
    }

    /**
     * Sets the callback to be executed when a mode is chosen.
     *
     * @param handler a {@link Consumer} that accepts the chosen {@link GameMode}.
     */
    public void setOnModeChosen(Consumer<GameMode> handler) {
        this.onModeChosen = handler;
    }

    /**
     * Sets the callback to be executed when the selection is cancelled.
     *
     * @param handler a {@link Runnable} to execute on cancel.
     */
    public void setOnCancel(Runnable handler) {
        this.onCancelHandler = handler;
    }
}