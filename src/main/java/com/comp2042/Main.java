package com.comp2042;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load bundled PressStart2P font before loading FXML so label styles can pick it up.
        // Prefer the resources/fonts/ location (created), fall back to root if not present.
        try {
            java.io.InputStream fontStream = getClass().getClassLoader().getResourceAsStream("fonts/PressStart2P-Regular.ttf");
            if (fontStream == null) {
                // fallback to the root of resources
                fontStream = getClass().getClassLoader().getResourceAsStream("PressStart2P-Regular.ttf");
            }
            if (fontStream != null) {
                javafx.scene.text.Font loaded = javafx.scene.text.Font.loadFont(fontStream, 12);
                if (loaded == null) {
                    System.err.println("Warning: Press Start 2P font failed to load from resources");
                }
            } else {
                System.err.println("Warning: PressStart2P-Regular.ttf not found in resources/fonts or root");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        URL location = getClass().getClassLoader().getResource("main_menu.fxml");
        ResourceBundle resources = null;
        FXMLLoader fxmlLoader = new FXMLLoader(location, resources);
        Parent root = fxmlLoader.load();
        MainMenuController controller = fxmlLoader.getController();
        controller.setPrimaryStage(primaryStage);

        primaryStage.setTitle("TetrisJFX");
        Scene scene = new Scene(root, 700, 600);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
