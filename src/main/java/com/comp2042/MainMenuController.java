package com.comp2042;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class MainMenuController {

    private Stage primaryStage;

    void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    @FXML
    private void onStartGame() {
        if (primaryStage == null) {
            return;
        }
        try {
            URL location = getClass().getClassLoader().getResource("gameLayout.fxml");
            FXMLLoader loader = new FXMLLoader(location);
            Parent root = loader.load();
            GuiController guiController = loader.getController();
            Scene gameScene = new Scene(root, 300, 510);
            primaryStage.setScene(gameScene);
            primaryStage.centerOnScreen();
            primaryStage.setTitle("TetrisJFX");
            primaryStage.show();
            new GameController(guiController);
        } catch (IOException e) {
            showInfo("Error", "Unable to start the game: " + e.getMessage());
        }
    }

    @FXML
    private void onTutorial() {
        showInfo("Tutorial", "Use Left/Right to move, Up to rotate, Down to drop faster, N for new game, P to pause.");
    }

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
}
