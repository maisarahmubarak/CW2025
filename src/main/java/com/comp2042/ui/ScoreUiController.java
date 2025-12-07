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
    public ScoreUiController(Label scoreLabel, Label highScoreLabel) {
        this.scoreLabel = scoreLabel;
        this.highScoreLabel = highScoreLabel;
    }
    
    /**
     * Loads the high score from disk on initialization.
     * Should be called during controller setup.
     */
    public void initialize() {
        loadHighScore();
        updateHighScoreLabel();
    }
    
    /**
     * Binds the score label to the game score and sets up high score tracking.
     * When the score exceeds the high score, it automatically updates and saves.
     * 
     * @param score The GameScore object to bind to
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
     */
    private void updateHighScoreLabel() {
        if (highScoreLabel != null) {
            highScoreLabel.setText(String.valueOf(highScore));
        }
    }
    
    /**
     * Loads the high score from the persistent file.
     * If the file doesn't exist or cannot be read, the high score remains at 0.
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
     * @return The current high score
     */
    public int getHighScore() {
        return highScore;
    }
}
