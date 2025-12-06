package com.comp2042;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class GameOverPanel extends BorderPane {

    private final Label finalScoreLabel;
    private final Button restartButton;
    private final Button mainMenuButton;
    private Runnable onRestart;
    private Runnable onMainMenu;

    public GameOverPanel() {
        // Use a StackPane to place a semi-transparent overlay behind the banner
        StackPane root = new StackPane();
        root.setPrefSize(215, 520); // match the game board area

        Rectangle overlay = new Rectangle();
        overlay.setFill(Color.rgb(0, 0, 0, 0.7));
        overlay.getStyleClass().add("game-over-overlay");
        // Bind overlay to the panel size so text doesn't clip
        overlay.widthProperty().bind(this.widthProperty());
        overlay.heightProperty().bind(this.heightProperty());
        overlay.getStyleClass().add("game-over-overlay");

        VBox content = new VBox(12);
        content.setAlignment(Pos.CENTER);

        final Label gameOverLabel = new Label("GAME OVER");
        gameOverLabel.getStyleClass().add("gameOverStyle");
        gameOverLabel.setWrapText(true);
        gameOverLabel.setAlignment(Pos.CENTER);
        // Make sure the label doesn't exceed the panel's width and gets centered
        gameOverLabel.maxWidthProperty().bind(this.widthProperty().subtract(32));

        finalScoreLabel = new Label("Score: 0");
        finalScoreLabel.setAlignment(Pos.CENTER);
        finalScoreLabel.setWrapText(true);
        finalScoreLabel.maxWidthProperty().bind(this.widthProperty().subtract(48));
        finalScoreLabel.getStyleClass().add("finalScoreBox");

        // Buttons - restart and back to main menu
        restartButton = new Button("Restart");
        restartButton.getStyleClass().add("menu-button");
        restartButton.setOnAction(e -> { if (onRestart != null) onRestart.run(); });
        mainMenuButton = new Button("Main Menu");
        mainMenuButton.getStyleClass().add("menu-button");
        mainMenuButton.setOnAction(e -> { if (onMainMenu != null) onMainMenu.run(); });
        HBox buttons = new HBox(12, restartButton, mainMenuButton);
        buttons.setAlignment(Pos.CENTER);

        // Keep previous behavior: use fixed sizes and no dynamic font scaling
        content.getChildren().addAll(gameOverLabel, finalScoreLabel, buttons);
        root.getChildren().addAll(overlay, content);
        // overlay is already bound; use fixed container size for GameOverPanel
        setCenter(root);
    }

    public void setFinalScore(int score) {
        finalScoreLabel.setText(String.format("SCORE: %d", score));
    }

    public void setOnRestart(Runnable r) {
        this.onRestart = r;
    }

    public void setOnMainMenu(Runnable r) {
        this.onMainMenu = r;
    }

}
