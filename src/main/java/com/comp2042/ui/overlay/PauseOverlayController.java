package com.comp2042.ui.overlay;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Controller for pause menu overlay.
 * <p>
 * PauseOverlayController manages the pause overlay UI.
 * Responsibilities:
 * <ul>
 *   <li>Creating pause overlay with Resume, Restart, and Main Menu buttons</li>
 *   <li>Showing and hiding the pause overlay</li>
 *   <li>Wiring pause overlay button actions to callbacks</li>
 * </ul>
 *
 * @author Maisarah
 * @version 1.0
 */
public class PauseOverlayController {
    
    private final javafx.scene.layout.Pane rootPane;
    private final AnchorPane groupNotification;
    
    private StackPane pauseOverlay;
    private Label pauseLabel;
    private Button pauseResumeButton;
    private Button pauseMainMenuButton;
    private Button pauseRestartButton;
    
    private Runnable onTogglePause;
    private Runnable onNewGame;
    private Runnable onReturnToMainMenu;
    
    /**
     * Constructor for PauseOverlayController.
     * 
     * @param rootPane The root pane for overlay positioning
     * @param groupNotification The notification overlay pane (fallback)
     */
    public PauseOverlayController(javafx.scene.layout.Pane rootPane, 
                                  AnchorPane groupNotification) {
        this.rootPane = rootPane;
        this.groupNotification = groupNotification;
    }
    
    /**
     * Sets the toggle pause callback.
     * 
     * @param callback The callback to execute when pause is toggled
     */
    public void setOnTogglePause(Runnable callback) {
        this.onTogglePause = callback;
    }
    
    /**
     * Sets the new game callback.
     * 
     * @param callback The callback to execute when starting a new game
     */
    public void setOnNewGame(Runnable callback) {
        this.onNewGame = callback;
    }
    
    /**
     * Sets the return to main menu callback.
     * 
     * @param callback The callback to execute when returning to main menu
     */
    public void setOnReturnToMainMenu(Runnable callback) {
        this.onReturnToMainMenu = callback;
    }
    
    /**
     * Initializes the pause overlay.
     */
    public void initialize() {
        createPauseOverlay();
    }
    
    /**
     * Shows the pause overlay.
     */
    public void show() {
        if (pauseOverlay != null) {
            pauseOverlay.setVisible(true);
        }
    }

    /**
     * Hides the pause overlay.
     */
    public void hide() {
        if (pauseOverlay != null) {
            pauseOverlay.setVisible(false);
        }
    }
    
    /**
     * Creates the pause overlay with Resume, Restart, and Main Menu buttons.
     */
    private void createPauseOverlay() {
        pauseOverlay = new StackPane();
        pauseOverlay.setVisible(false);
        pauseOverlay.setPickOnBounds(true);

        Rectangle rect = new Rectangle();
        rect.setFill(Color.rgb(0, 0, 0, 0.65));
        if (rootPane != null) {
            rect.widthProperty().bind(rootPane.widthProperty());
            rect.heightProperty().bind(rootPane.heightProperty());
        } else {
            rect.setWidth(215);
            rect.setHeight(520);
        }
        rect.getStyleClass().add("pause-overlay");

        VBox content = new VBox(16);
        content.setAlignment(javafx.geometry.Pos.CENTER);
        pauseLabel = new Label("PAUSED");
        pauseLabel.getStyleClass().add("pauseLabel");
        
        pauseResumeButton = new Button("Resume");
        pauseResumeButton.getStyleClass().addAll("menu-button", "resume-button", "pause-button");
        pauseResumeButton.setOnAction(e -> {
            if (onTogglePause != null) {
                onTogglePause.run();
            }
        });
        pauseResumeButton.setPrefWidth(180);
        pauseResumeButton.setMinWidth(180);
        pauseResumeButton.setMaxWidth(180);
        pauseResumeButton.setPrefHeight(48);
        
        pauseRestartButton = new Button("Restart");
        pauseRestartButton.getStyleClass().addAll("menu-button", "restart-button", "pause-button");
        pauseRestartButton.setPrefWidth(180);
        pauseRestartButton.setMinWidth(180);
        pauseRestartButton.setMaxWidth(180);
        pauseRestartButton.setPrefHeight(48);
        pauseRestartButton.setOnAction(e -> {
            if (onNewGame != null) {
                onNewGame.run();
            }
        });

        pauseMainMenuButton = new Button("Main Menu");
        pauseMainMenuButton.getStyleClass().addAll("menu-button", "quit-button", "pause-button");
        pauseMainMenuButton.setPrefWidth(180);
        pauseMainMenuButton.setMinWidth(180);
        pauseMainMenuButton.setMaxWidth(180);
        pauseMainMenuButton.setPrefHeight(48);
        pauseMainMenuButton.setOnAction(e -> {
            if (onReturnToMainMenu != null) {
                onReturnToMainMenu.run();
            }
        });
        
        HBox buttons = new HBox(20, pauseResumeButton, pauseRestartButton, pauseMainMenuButton);
        buttons.setAlignment(javafx.geometry.Pos.CENTER);
        buttons.setPadding(new javafx.geometry.Insets(0, 20, 0, 20));
        content.getChildren().addAll(pauseLabel, buttons);
        pauseOverlay.getChildren().addAll(rect, content);
        
        if (rootPane != null) {
            rootPane.getChildren().add(pauseOverlay);
        } else if (groupNotification != null) {
            groupNotification.getChildren().add(pauseOverlay);
        }
    }
}
