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
 * Controller for game over overlay.
 * <p>
 * GameOverOverlayController manages the game over overlay UI.
 * Responsibilities:
 * <ul>
 *   <li>Creating game over overlay with score display and action buttons</li>
 *   <li>Showing and hiding the game over overlay</li>
 *   <li>Updating the final score display</li>
 *   <li>Wiring game over overlay button actions to callbacks</li>
 * </ul>
 * </p>
 *
 * @author Maisarah
 * @version 1.0
 */
public class GameOverOverlayController {
    
    private final javafx.scene.layout.Pane rootPane;
    private final AnchorPane groupNotification;
    private final GameOverPanel gameOverPanel;
    
    private StackPane gameOverOverlay;
    private Label gameOverTitleLabel;
    private Label gameOverScoreLabel;
    private Button gameOverRestartButton;
    private Button gameOverMainMenuButton;
    
    private Runnable onNewGame;
    private Runnable onReturnToMainMenu;
    
    /**
     * Constructor for GameOverOverlayController.
     * 
     * @param rootPane The root pane for overlay positioning
     * @param groupNotification The notification overlay pane (fallback)
     * @param gameOverPanel The legacy game over panel
     */
    public GameOverOverlayController(javafx.scene.layout.Pane rootPane,
                                    AnchorPane groupNotification,
                                    GameOverPanel gameOverPanel) {
        this.rootPane = rootPane;
        this.groupNotification = groupNotification;
        this.gameOverPanel = gameOverPanel;
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
     * Initializes the game over overlay.
     */
    public void initialize() {
        createGameOverOverlay();
        wireGameOverButtons();
    }
    
    /**
     * Shows the game over overlay.
     */
    public void show() {
        if (gameOverOverlay != null) {
            gameOverOverlay.setVisible(true);
        } else {
            gameOverPanel.setVisible(true);
        }
    }
    
    /**
     * Hides the game over overlay.
     */
    public void hide() {
        if (gameOverOverlay != null) {
            gameOverOverlay.setVisible(false);
        }
        gameOverPanel.setVisible(false);
    }
    
    /**
     * Sets the final score on the game over overlay.
     * 
     * @param score The final score to display
     */
    public void setFinalScore(int score) {
        try {
            if (gameOverPanel != null) {
                gameOverPanel.setFinalScore(score);
            }
            if (gameOverScoreLabel != null) {
                gameOverScoreLabel.setText(String.format("SCORE: %d", score));
            }
        } catch (Exception ignored) {
        }
    }
    
    /**
     * Creates the game over overlay with score display and Restart/Main Menu buttons.
     */
    private void createGameOverOverlay() {
        gameOverOverlay = new StackPane();
        gameOverOverlay.setVisible(false);
        gameOverOverlay.setPickOnBounds(true);

        Rectangle rect = new Rectangle();
        rect.setFill(Color.rgb(0, 0, 0, 0.65));
        if (rootPane != null) {
            rect.widthProperty().bind(rootPane.widthProperty());
            rect.heightProperty().bind(rootPane.heightProperty());
        } else {
            rect.setWidth(215);
            rect.setHeight(520);
        }
        rect.getStyleClass().add("game-over-overlay");

        VBox content = new VBox(16);
        content.setAlignment(javafx.geometry.Pos.CENTER);

        gameOverTitleLabel = new Label("GAME OVER");
        gameOverTitleLabel.getStyleClass().add("gameOverStyle");
        gameOverTitleLabel.setWrapText(true);

        gameOverScoreLabel = new Label("SCORE: 0");
        gameOverScoreLabel.getStyleClass().add("finalScoreBox");

        gameOverRestartButton = new Button("Restart");
        gameOverRestartButton.getStyleClass().addAll("menu-button", "restart-button", "pause-button");
        gameOverRestartButton.setPrefWidth(180);
        gameOverRestartButton.setMinWidth(180);
        gameOverRestartButton.setMaxWidth(180);
        gameOverRestartButton.setPrefHeight(48);
        gameOverRestartButton.setOnAction(e -> {
            if (onNewGame != null) {
                onNewGame.run();
            }
        });

        gameOverMainMenuButton = new Button("Main Menu");
        gameOverMainMenuButton.getStyleClass().addAll("menu-button", "main-menu-button", "pause-button");
        gameOverMainMenuButton.setPrefWidth(180);
        gameOverMainMenuButton.setMinWidth(180);
        gameOverMainMenuButton.setMaxWidth(180);
        gameOverMainMenuButton.setPrefHeight(48);
        gameOverMainMenuButton.setOnAction(e -> { 
            if (onReturnToMainMenu != null) {
                onReturnToMainMenu.run();
            }
        });

        HBox buttons = new HBox(20, gameOverRestartButton, gameOverMainMenuButton);
        buttons.setAlignment(javafx.geometry.Pos.CENTER);
        buttons.setPadding(new javafx.geometry.Insets(0, 20, 0, 20));

        content.getChildren().addAll(gameOverTitleLabel, gameOverScoreLabel, buttons);
        gameOverOverlay.getChildren().addAll(rect, content);

        if (rootPane != null) {
            rootPane.getChildren().add(gameOverOverlay);
        } else if (groupNotification != null) {
            groupNotification.getChildren().add(gameOverOverlay);
        }
    }
    
    /**
     * Wires the game over panel buttons to callbacks.
     */
    private void wireGameOverButtons() {
        if (gameOverPanel == null) return;
        gameOverPanel.setOnRestart(() -> {
            if (onNewGame != null) {
                onNewGame.run();
            }
        });
        gameOverPanel.setOnMainMenu(() -> {
            if (onReturnToMainMenu != null) {
                onReturnToMainMenu.run();
            }
        });

        if (gameOverRestartButton != null) {
            gameOverRestartButton.setOnAction(e -> {
                if (onNewGame != null) {
                    onNewGame.run();
                }
            });
        }
        if (gameOverMainMenuButton != null) {
            gameOverMainMenuButton.setOnAction(e -> { 
                if (onReturnToMainMenu != null) {
                    onReturnToMainMenu.run();
                }
            });
        }
    }
}
