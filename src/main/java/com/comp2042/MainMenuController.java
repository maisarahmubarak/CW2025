package com.comp2042;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import javafx.stage.Modality;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Region;
import javafx.geometry.Pos;

public class MainMenuController {

	private Stage primaryStage;

	@FXML
	private Pane animationLayer;

	@FXML
	private void initialize() {
	}

	void setPrimaryStage(Stage stage) {
		this.primaryStage = stage;
	}

	@FXML
	private void onStartGame() {
		if (primaryStage == null) {
			return;
		}
		try {
			URL modeLocation = getClass().getClassLoader().getResource("mode_selection.fxml");
			if (modeLocation == null) {
				showInfo("Error", "Mode selection dialog couldn't be loaded: resource not found.");
				return;
			}
			FXMLLoader modeLoader = new FXMLLoader(modeLocation);
			Parent modeRoot = modeLoader.load();
			ModeSelectionController modeController = modeLoader.getController();
			modeController.setInitialMode(selectedMode);
			// use overlay in same primary Stage
			if (animationLayer == null) {
				// fallback to modal stage if overlay not available
				Stage modeStage = new Stage();
				modeStage.setScene(new Scene(modeRoot));
				modeStage.initOwner(primaryStage);
				modeStage.initModality(Modality.APPLICATION_MODAL);
				modeStage.setTitle("Select Mode");
				modeStage.showAndWait();
				GameMode selectedMode = modeController.getSelectedMode();
				if (selectedMode == null) {
					return;
				}
				this.selectedMode = selectedMode;
				startGameWithMode(selectedMode);
				return;
			}
			// Add semi-transparent dim to block the menu and focus the modal
			javafx.scene.shape.Rectangle dim = new javafx.scene.shape.Rectangle();
			dim.setFill(javafx.scene.paint.Color.rgb(0, 0, 0, 0.55));
			dim.widthProperty().bind(animationLayer.widthProperty());
			dim.heightProperty().bind(animationLayer.heightProperty());
			StackPane overlay = new StackPane(dim, modeRoot);
			overlay.setPrefSize(animationLayer.getPrefWidth(), animationLayer.getPrefHeight());
			overlay.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
			overlay.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
			// center modeRoot in overlay
			StackPane.setAlignment(modeRoot, Pos.CENTER);
			animationLayer.getChildren().add(overlay);
			// Centering should work because modeRoot is inside a StackPane and prefers center alignment
			modeController.setParentOverlay(animationLayer);
			modeController.setOnCancel(() -> {
				animationLayer.getChildren().remove(overlay);
			});
			modeController.setOnModeChosen(modeSelected -> {
				animationLayer.getChildren().remove(overlay);
				this.selectedMode = modeSelected;
				startGameWithMode(modeSelected);
			});
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

	private GameMode selectedMode = GameMode.CLASSIC;

	@FXML
	private void onSettings() {
		showInfo("Settings", "Settings panel coming soon.");
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

	private void startGameWithMode(GameMode selectedMode) {
		try {
			URL location = getClass().getClassLoader().getResource("gameLayout.fxml");
			if (location == null) {
				showInfo("Error", "Game view couldn't be loaded: resource not found.");
				return;
			}
			FXMLLoader loader = new FXMLLoader(location);
			Parent root = loader.load();
			GuiController guiController = loader.getController();
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
			GameController gameController = new GameController(guiController, selectedMode);
			// Ensure GameOver panel's Main Menu button returns to main menu
			guiController.setOnReturnToMainMenu(() -> {
				try {
					URL menuLocation = getClass().getClassLoader().getResource("main_menu.fxml");
					if (menuLocation != null) {
						FXMLLoader menuLoader = new FXMLLoader(menuLocation);
						Parent menuRoot = menuLoader.load();
						MainMenuController mainController = menuLoader.getController();
						mainController.setPrimaryStage(primaryStage);
						Scene menuScene = new Scene(menuRoot, 700, 600);
						primaryStage.setScene(menuScene);
						primaryStage.centerOnScreen();
						primaryStage.setTitle("TetrisJFX");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
			showInfo("Error", "Unable to start the game: " + e.getMessage());
		}
	}
}
