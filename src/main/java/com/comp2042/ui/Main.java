package com.comp2042.ui;

import com.comp2042.ui.menu.MainMenuController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * The main entry point for the TetrisJFX application.
 * <p>
 * This class extends {@link javafx.application.Application} and is responsible for setting up the primary stage,
 * loading the initial scene (Main Menu), and launching the JavaFX application.
 * It also handles the loading of custom fonts required by the application's UI.
 * </p>
 */
public class Main extends Application {

    /**
     * Starts the JavaFX application.
     * <p>
     * This method initializes the primary stage, loads the custom "Press Start 2P" font,
     * loads the main menu FXML layout, and displays the application window.
     * </p>
     *
     * @param primaryStage the primary stage for this application, onto which
     *                     the application scene can be set.
     * @throws Exception if an error occurs during the loading of resources or FXML files.
     */
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


    /**
     * The main method serves as the entry point for the Java application.
     * <p>
     * It calls the {@link #launch(String...)} method to start the JavaFX lifecycle.
     * </p>
     *
     * @param args the command line arguments passed to the application.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
