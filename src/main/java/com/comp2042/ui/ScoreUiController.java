package com.comp2042.ui;

import com.comp2042.logic.game.GameScore;
import javafx.scene.control.Label;

import java.io.*;

/**
 * ScoreUiController handles all score display and high-score persistence responsibilities.
 * This class is responsible for:
 * - Binding the score label to the game score
 * - Tracking and updating the high score
 * - Loading and saving the high score to disk
 * - Updating the high score UI label
 */
public class ScoreUiController {
    
    private static int highScore = 0;
    private static final String HIGH_SCORE_FILE = "highscore.dat";
    
    private Label scoreLabel;
    private Label highScoreLabel;
    
    /**
     * Constructor for ScoreUiController.
     * 
     * @param scoreLabel The label displaying the current score
     * @param highScoreLabel The label displaying the high score
     */
    /**
     * Constructs a new ScoreUiController.
     * <p>
     * Initializes the controller with the UI labels for displaying scores.
     * </p>
     * 
     * @param scoreLabel The label displaying the current score.
     * @param highScoreLabel The label displaying the high score.
     */
    public ScoreUiController(Label scoreLabel, Label highScoreLabel) {
        this.scoreLabel = scoreLabel;
        this.highScoreLabel = highScoreLabel;
    }
    
    /**
     * Initializes the score controller.
     * <p>
     * Loads the high score from disk and updates the high score label.
     * This method should be called during the application startup or controller initialization.
     * </p>
     */
    public void initialize() {
        loadHighScore();
        updateHighScoreLabel();
    }
    
    /**
     * Binds the score label to the game score and sets up high score tracking.
     * <p>
     * Connects the UI label to the GameScore property so it updates automatically.
     * Also adds a listener to check if the new score exceeds the high score, updating and saving it if so.
     * </p>
     * 
     * @param score The GameScore object to bind to.
     */
    public void bindToScore(GameScore score) {
        if (score == null) {
            return;
        }
        scoreLabel.textProperty().bind(score.scoreProperty().asString());
        score.scoreProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.intValue() > highScore) {
                highScore = newVal.intValue();
                updateHighScoreLabel();
                saveHighScore();
            }
        });
        updateHighScoreLabel();
    }
    
    /**
     * Updates the high score label with the current high score value.
     * <p>
     * Sets the text of the high score label to the current high score.
     * </p>
     */
    private void updateHighScoreLabel() {
        if (highScoreLabel != null) {
            highScoreLabel.setText(String.valueOf(highScore));
        }
    }
    
    /**
     * Loads the high score from the persistent file.
     * <p>
     * Reads the high score from "highscore.dat". If the file doesn't exist or an error occurs,
     * the high score defaults to 0.
     * </p>
     */
    private void loadHighScore() {
        File file = new File(HIGH_SCORE_FILE);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line = reader.readLine();
                if (line != null) {
                    highScore = Integer.parseInt(line.trim());
                }
            } catch (IOException | NumberFormatException e) {
                System.err.println("Failed to load high score: " + e.getMessage());
            }
        }
    }
    
    /**
     * Saves the current high score to the persistent file.
     * <p>
     * Writes the current high score to "highscore.dat".
     * </p>
     */
    private void saveHighScore() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HIGH_SCORE_FILE))) {
            writer.write(String.valueOf(highScore));
        } catch (IOException e) {
            System.err.println("Failed to save high score: " + e.getMessage());
        }
    }
    
    /**
     * Gets the current high score value.
     * 
     * @return The current high score.
     */
    public int getHighScore() {
        return highScore;
    }
}
