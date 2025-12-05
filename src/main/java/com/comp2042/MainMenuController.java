package com.comp2042;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.Random;
import javafx.stage.Modality;

public class MainMenuController {

	private Stage primaryStage;
	private Timeline blockTimeline;
	private final Random random = new Random();

	@FXML
	private Pane animationLayer;

	@FXML
	private void initialize() {
		startBlockRain();
	}

	void setPrimaryStage(Stage stage) {
		this.primaryStage = stage;
	}

	@FXML
	private void onStartGame() {
		stopBlockRain();
		if (primaryStage == null) {
			return;
		}
		try {
			// First show a mode selection dialog
			URL modeLocation = getClass().getClassLoader().getResource("mode_selection.fxml");
			if (modeLocation == null) {
				showInfo("Error", "Mode selection dialog couldn't be loaded: resource not found.");
			} else {
				FXMLLoader modeLoader = new FXMLLoader(modeLocation);
				Parent modeRoot = modeLoader.load();
				ModeSelectionController modeController = modeLoader.getController();
				Stage modeStage = new Stage();
				modeStage.setScene(new Scene(modeRoot));
				modeStage.initOwner(primaryStage);
				modeStage.initModality(Modality.APPLICATION_MODAL);
				modeStage.setTitle("Select Mode");
				modeStage.showAndWait();
				GameMode selectedMode = modeController.getSelectedMode();
				if (selectedMode == null) {
					// user canceled, just return
					startBlockRain();
					return;
				}
				// otherwise proceed to start the game using the selected mode
				URL location = getClass().getClassLoader().getResource("gameLayout.fxml");
				if (location == null) {
					showInfo("Error", "Game view couldn't be loaded: resource not found.");
					return;
				}
				FXMLLoader loader = new FXMLLoader(location);
				Parent root = loader.load();
				GuiController guiController = loader.getController();
				// pass the selected mode to GuiController if it supports it
				try {
					guiController.setGameMode(selectedMode);
				} catch (NoSuchMethodError | RuntimeException ignored) {
					// If controller doesn't support game mode, ignore gracefully
				}
				Scene gameScene = new Scene(root, 700, 600);
				primaryStage.setScene(gameScene);
				primaryStage.centerOnScreen();
				primaryStage.setTitle("TetrisJFX - " + selectedMode.name());
				primaryStage.show();
				new GameController(guiController);
			}
		} catch (Exception e) {
			// Print stack to console to aid debugging and show a user-friendly message
			e.printStackTrace();
			showInfo("Error", "Unable to start the game: " + e.getMessage());
		}
	}

	@FXML
	private void onTutorial() {
		showInfo("Tutorial", "Use Left/Right to move, Up to rotate, Down to drop faster, N for new game, P to pause.");
	}

	@FXML
	private void onModes() {
		showInfo("Modes", "Mode selection coming soon—choose Classic/Speed/Danger when available.");
	}

	@FXML
	private void onSettings() {
		showInfo("Settings", "Settings panel coming soon.");
	}

	private void startBlockRain() {
		blockTimeline = new Timeline(
				new KeyFrame(Duration.seconds(0.3), event -> spawnBlock())
		);
		blockTimeline.setCycleCount(Timeline.INDEFINITE);
		blockTimeline.play();
	}

	private void stopBlockRain() {
		if (blockTimeline != null) {
			blockTimeline.stop();
			blockTimeline = null;
		}
		if (animationLayer != null) {
			animationLayer.getChildren().clear();
		}
	}

	private void spawnBlock() {
		if (animationLayer == null) {
			return;
		}
		double width = animationLayer.getWidth();
		double height = animationLayer.getHeight();
		if (width <= 0 || height <= 0) {
			return;
		}
		double size = 18 + random.nextInt(18);
		Rectangle block = new Rectangle(size, size);
		block.setArcHeight(6);
		block.setArcWidth(6);
		block.setFill(randomColor());
		block.setOpacity(0.75);
		double x = random.nextDouble() * (width - size);
		block.setLayoutX(Math.max(0, x));
		block.setLayoutY(-40);
		animationLayer.getChildren().add(block);

		TranslateTransition fall = new TranslateTransition(Duration.seconds(4 + random.nextDouble() * 2), block);
		fall.setFromY(0);
		fall.setToY(height + 80);
		fall.setInterpolator(Interpolator.LINEAR);
		fall.setOnFinished(e -> animationLayer.getChildren().remove(block));
		fall.play();
	}

	private Color randomColor() {
		Color[] palette = new Color[]{
				Color.AQUA, Color.BLUEVIOLET, Color.DARKORANGE,
				Color.LIMEGREEN, Color.GOLD, Color.HOTPINK,
				Color.DEEPSKYBLUE
		};
		return palette[random.nextInt(palette.length)];
	}

	private void showInfo(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		if (primaryStage != null) {
			alert.initOwner(primaryStage);
		}
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}
