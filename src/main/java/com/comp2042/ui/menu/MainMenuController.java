package com.comp2042.ui.menu;

import com.comp2042.ui.effects.NeonGridBackground;
import com.comp2042.ui.effects.RetroParticleBackground;
import com.comp2042.ui.overlay.GameOverPanel;
import com.comp2042.ui.effects.TitleRevealAnimator;
import com.comp2042.logic.game.GameMode;
import com.comp2042.logic.game.GameSettings;
import com.comp2042.ui.GameController;
import com.comp2042.ui.GuiController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import javafx.stage.Modality;
import javafx.scene.layout.StackPane;
// animation imports moved to TitleRevealAnimator
import static com.comp2042.ui.effects.TitleRevealAnimator.startOnce;
import javafx.scene.layout.Region;
import javafx.geometry.Pos;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * Controller for the main menu screen of the application.
 * <p>
 * This class manages the main entry point of the user interface, providing navigation to:
 * <ul>
 *   <li>New Game (Mode Selection)</li>
 *   <li>Settings</li>
 *   <li>Tutorial</li>
 *   <li>High Scores (Scoreboard)</li>
 * </ul>
 * It also initializes and manages visual effects such as the {@link NeonGridBackground},
 * {@link RetroParticleBackground}, and the title reveal animation.
 *
 * @author Maisarah
 * @version 1.0
 */
public class MainMenuController {

	private Stage primaryStage;
	private MediaPlayer mediaPlayer;

	@FXML
	private Pane animationLayer;

	@FXML
	private Pane particleLayer;

	private RetroParticleBackground particleBackground;

	@FXML
	private Label titleLabel;

	// Animation state
	// Title reveal animation uses TitleRevealAnimator; no per-controller RNG required here

	/**
	 * Initializes the controller class.
	 * <p>
	 * This method is automatically called after the FXML file has been loaded. It performs the following:
	 * <ul>
	 *   <li>Loads the custom "Press Start 2P" font.</li>
	 *   <li>Applies styling to the title label.</li>
	 *   <li>Initializes the particle background effect.</li>
	 *   <li>Starts the title reveal animation.</li>
	 *   <li>Starts the background music.</li>
	 * </ul>
	 * </p>
	 */
	@FXML
	private void initialize() {
		// Ensure 'Press Start 2P' is available in this app by loading the bundled TTF file.
		try {
			java.io.InputStream fontStream = getClass().getClassLoader().getResourceAsStream("fonts/PressStart2P-Regular.ttf");
			if (fontStream == null) {
				fontStream = getClass().getClassLoader().getResourceAsStream("PressStart2P-Regular.ttf");
			}
			if (fontStream != null) {
				javafx.scene.text.Font.loadFont(fontStream, 12);
			}
		} catch (Exception ignored) {}

		// Ensure the title label uses the new style (if not properly applied)
		if (titleLabel != null) {
			titleLabel.getStyleClass().removeIf(c -> c.equals("Press Start 2P"));
			if (!titleLabel.getStyleClass().contains("menu-title")) {
				titleLabel.getStyleClass().add("menu-title");
			}
			// Programmatic fallback: set font directly to avoid character blocks if CSS fails
			try {
				javafx.scene.text.Font custom = javafx.scene.text.Font.font("Press Start 2P", 110);
				if (custom != null) {
					titleLabel.setFont(custom);
				}
			} catch (Exception ignored) {}
		}
		
		if (animationLayer != null) {
			animationLayer.sceneProperty().addListener((obs, oldScene, newScene) -> {
				if (newScene != null) {
					applyBrightness();
				}
			});
		}
		
		// Initialize the retro particle background
		initParticleBackground();

		// Start the title reveal/flicker animation when the scene is ready
		if (titleLabel != null) {
			// Delay to allow CSS and layout to apply; use reusable animator
			javafx.application.Platform.runLater(() -> startOnce(titleLabel));
		}

		// Start background music
		playBackgroundMusic();
	}

	/**
	 * Plays the background music for the main menu.
	 * <p>
	 * Attempts to load "Tetris_Theme.wav" from the resources. If found, it plays the music
	 * in an indefinite loop. The volume is bound to the global game settings.
	 * </p>
	 */
	private void playBackgroundMusic() {
		try {
			// Try to find the music file (Tetris_Theme.wav)
			URL musicResource = getClass().getClassLoader().getResource("Tetris_Theme.wav");
			if (musicResource == null) {
				// Fallback: try looking in music/ folder if user moves it there
				musicResource = getClass().getClassLoader().getResource("music/Tetris-Theme.wav");
			}
			
			if (musicResource != null) {
				Media sound = new Media(musicResource.toURI().toString());
				mediaPlayer = new MediaPlayer(sound);
				mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
				
				// Bind volume to GameSettings (0-100 scale -> 0.0-1.0 scale)
				mediaPlayer.setVolume(GameSettings.getVolume() / 100.0);
				GameSettings.volumeProperty().addListener((obs, oldVal, newVal) -> {
					if (mediaPlayer != null) {
						mediaPlayer.setVolume(newVal.doubleValue() / 100.0);
					}
				});
				
				mediaPlayer.play();
			} else {
				System.out.println("Background music file not found.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Starts a one-time 'power-on' pixel-style reveal animation.
	 * Reveals the title in steps: "" → T → TE → TET → ... with ~100ms between steps.
	 * Adds a subtle random flicker frame before some steps.
	 */
	// MainMenuController now uses TitleRevealAnimator; no local animation code here.

	/**
	 * Applies the brightness setting to the scene.
	 * <p>
	 * Creates a {@link javafx.scene.effect.ColorAdjust} effect based on the current
	 * brightness setting and applies it to the root of the scene.
	 * </p>
	 */
	private void applyBrightness() {
		if (animationLayer != null && animationLayer.getScene() != null) {
			double sliderVal = GameSettings.getBrightness();
			double colorAdjustVal = sliderVal - 1.0;
			javafx.scene.effect.ColorAdjust adjust = new javafx.scene.effect.ColorAdjust();
			adjust.setBrightness(colorAdjustVal);
			animationLayer.getScene().getRoot().setEffect(adjust);
		}
	}

	/**
	 * Initializes the retro particle background with floating glowing particles.
	 * The particles are added to the particleLayer which sits behind all menu UI elements.
	 */
	private void initParticleBackground() {
		if (particleLayer == null) {
			return;
		}
		
		particleBackground = new RetroParticleBackground();
		
		// Bind the background size to the particle layer using min/max constraints
		// instead of prefWidth/Height bindings to avoid conflicts
		particleBackground.minWidthProperty().bind(particleLayer.widthProperty());
		particleBackground.maxWidthProperty().bind(particleLayer.widthProperty());
		particleBackground.minHeightProperty().bind(particleLayer.heightProperty());
		particleBackground.maxHeightProperty().bind(particleLayer.heightProperty());
		
		// Add to the particle layer
		particleLayer.getChildren().add(particleBackground);
		
		// Start the particle animation
		particleBackground.start();
	}

	/**
	 * Sets the primary stage for this controller.
	 *
	 * @param stage The primary stage of the application.
	 */
	public void setPrimaryStage(Stage stage) {
		this.primaryStage = stage;
	}

	/**
	 * Handles the "New Game" button click.
	 * <p>
	 * Opens the mode selection dialog. If a mode is chosen, it starts the game with that mode.
	 * The dialog is displayed as an overlay on top of the current menu.
	 * </p>
	 */
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

	/**
	 * Handles the "Tutorial" button click.
	 * <p>
	 * Opens the tutorial panel as an overlay on top of the current menu.
	 * </p>
	 */
	@FXML
	private void onTutorial() {
		if (primaryStage == null) {
			return;
		}
		try {
			URL tutorialLocation = getClass().getClassLoader().getResource("tutorial.fxml");
			if (tutorialLocation == null) {
				showInfo("Error", "Tutorial panel couldn't be loaded: resource not found.");
				return;
			}
			FXMLLoader tutorialLoader = new FXMLLoader(tutorialLocation);
			Parent tutorialRoot = tutorialLoader.load();
			TutorialController tutorialController = tutorialLoader.getController();

			// use overlay in same primary Stage
			if (animationLayer == null) {
				showInfo("Error", "Animation layer missing.");
				return;
			}

			// Add semi-transparent dim to block the menu and focus the modal
			javafx.scene.shape.Rectangle dim = new javafx.scene.shape.Rectangle();
			dim.setFill(javafx.scene.paint.Color.rgb(0, 0, 0, 0.55));
			dim.widthProperty().bind(animationLayer.widthProperty());
			dim.heightProperty().bind(animationLayer.heightProperty());
			StackPane overlay = new StackPane(dim, tutorialRoot);
			overlay.setPrefSize(animationLayer.getPrefWidth(), animationLayer.getPrefHeight());
			overlay.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
			overlay.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

			StackPane.setAlignment(tutorialRoot, Pos.CENTER);
			animationLayer.getChildren().add(overlay);

			tutorialController.setParentOverlay(animationLayer);
			tutorialController.setOnBack(() -> {
				animationLayer.getChildren().remove(overlay);
			});

		} catch (Exception e) {
			e.printStackTrace();
			showInfo("Error", "Unable to open tutorial: " + e.getMessage());
		}
	}

	private GameMode selectedMode = GameMode.CLASSIC;

	/**
	 * Handles the "Settings" button click.
	 * <p>
	 * Opens the settings panel as an overlay on top of the current menu.
	 * </p>
	 */
	@FXML
	private void onSettings() {
		if (primaryStage == null) {
			return;
		}
		try {
			URL settingsLocation = getClass().getClassLoader().getResource("settings.fxml");
			if (settingsLocation == null) {
				showInfo("Error", "Settings panel couldn't be loaded: resource not found.");
				return;
			}
			FXMLLoader settingsLoader = new FXMLLoader(settingsLocation);
			Parent settingsRoot = settingsLoader.load();
			SettingsController settingsController = settingsLoader.getController();

			// use overlay in same primary Stage
			if (animationLayer == null) {
				showInfo("Error", "Animation layer missing.");
				return;
			}

			// Add semi-transparent dim to block the menu and focus the modal
			javafx.scene.shape.Rectangle dim = new javafx.scene.shape.Rectangle();
			dim.setFill(javafx.scene.paint.Color.rgb(0, 0, 0, 0.55));
			dim.widthProperty().bind(animationLayer.widthProperty());
			dim.heightProperty().bind(animationLayer.heightProperty());
			StackPane overlay = new StackPane(dim, settingsRoot);
			overlay.setPrefSize(animationLayer.getPrefWidth(), animationLayer.getPrefHeight());
			overlay.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
			overlay.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

			StackPane.setAlignment(settingsRoot, Pos.CENTER);
			animationLayer.getChildren().add(overlay);

			settingsController.setParentOverlay(animationLayer);
			settingsController.setOnBack(() -> {
				animationLayer.getChildren().remove(overlay);
			});

		} catch (Exception e) {
			e.printStackTrace();
			showInfo("Error", "Unable to open settings: " + e.getMessage());
		}
	}

	/**
	 * Displays an information alert dialog.
	 *
	 * @param title   The title of the alert.
	 * @param message The content message of the alert.
	 */
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

	/**
	 * Starts the game with the specified game mode.
	 * <p>
	 * Loads the game layout, initializes the game controller, and switches the scene to the game view.
	 * </p>
	 *
	 * @param selectedMode The game mode to start.
	 */
	private void startGameWithMode(GameMode selectedMode) {
		// Stop music before starting game
		if (mediaPlayer != null) {
			mediaPlayer.stop();
		}
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
