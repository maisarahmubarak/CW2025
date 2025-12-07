package com.comp2042.ui.overlay;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * GameOverlayController is solely responsible for managing pause and game-over overlay UI:
 * - Creating pause overlay with buttons (Resume, Restart, Main Menu)
 * - Showing and hiding pause overlay
 * - Creating game-over overlay with score display and buttons
 * - Wiring all overlay button actions
 * 
 * This controller handles all overlay UI creation, visibility management, and button wiring,
 * keeping overlay presentation logic separate from game lifecycle logic.
 */
public class GameOverlayController {
    
    private final GridPane gamePanel;
    private final AnchorPane groupNotification;
    private final GameOverPanel gameOverPanel;
    private final javafx.scene.layout.Pane rootPane;
    
    // Pause overlay UI
    private StackPane pauseOverlay;
    private Label pauseLabel;
    private Button pauseResumeButton;
    private Button pauseMainMenuButton;
    private Button pauseRestartButton;
    
    // Game over overlay UI
    private StackPane gameOverOverlay;
    private Label gameOverTitleLabel;
    private Label gameOverScoreLabel;
    private Button gameOverRestartButton;
    private Button gameOverMainMenuButton;
    
    // Callbacks for button actions
    private Runnable onTogglePause;
    private Runnable onNewGame;
    private Runnable onReturnToMainMenu;
    
    /**
     * Constructor for GameOverlayController.
     * 
     * @param gamePanel The game panel for layout
     * @param groupNotification The notification overlay pane
     * @param gameOverPanel The legacy game over panel
     * @param rootPane The root pane for overlay positioning
     */
    public GameOverlayController(GridPane gamePanel, 
                                 AnchorPane groupNotification,
                                 GameOverPanel gameOverPanel, 
                                 javafx.scene.layout.Pane rootPane) {
        this.gamePanel = gamePanel;
        this.groupNotification = groupNotification;
        this.gameOverPanel = gameOverPanel;
        this.rootPane = rootPane;
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
     * Initializes all pause and game over overlays.
     */
    public void initializeOverlays() {
        createPauseOverlay();
        createGameOverOverlay();
        wireGameOverButtons();
    }
    
    /**
     * Shows the pause overlay.
     */
    public void showPauseOverlay() {
        if (pauseOverlay == null) return;
        pauseOverlay.setVisible(true);
    }

    /**
     * Hides the pause overlay.
     */
    public void hidePauseOverlay() {
        if (pauseOverlay == null) return;
        pauseOverlay.setVisible(false);
    }
    
    /**
     * Shows the game over overlay.
     */
    public void showGameOverOverlay() {
        if (gameOverOverlay != null) {
            gameOverOverlay.setVisible(true);
        } else {
            gameOverPanel.setVisible(true);
        }
    }
    
    /**
     * Hides the game over overlay.
     */
    public void hideGameOverOverlay() {
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
