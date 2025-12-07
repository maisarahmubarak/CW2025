package com.comp2042.ui.effects;

import com.comp2042.BrickColorPalette;
import com.comp2042.logic.board.ClearRow;
import com.comp2042.logic.game.GameSettings;
import com.comp2042.ui.GameBoardView;
import com.comp2042.ui.JavaFxBoardView;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.AudioClip;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.Random;

/**
 * BoardAnimationController handles all visual animations and sound effects for row clears.
 * This class is responsible for:
 * - Playing row clear sound effects
 * - Creating visual falling/fading animations for cleared row blocks
 * - Board shake effects for multi-row clears
 * 
 * This controller has no game logic - it only handles pure visual and audio effects.
 */
public class BoardAnimationController {
    
    private static final int BRICK_SIZE = 20;
    
    private final StackPane boardStack;
    private final AnchorPane groupNotification;
    private final GameBoardView gameBoardView;
    private final BrickColorPalette palette;
    
    private AudioClip rowClearSound;
    private Random animationRandom = new Random(); // Used exclusively for animation randomness
    
    /**
     * Constructor for BoardAnimationController.
     * 
     * @param boardStack The stack pane containing the game board
     * @param groupNotification The notification overlay pane
     * @param gameBoardView The game board view for position calculations
     * @param palette The color palette for rendering brick colors
     */
    public BoardAnimationController(StackPane boardStack, 
                                    AnchorPane groupNotification,
                                    GameBoardView gameBoardView, 
                                    BrickColorPalette palette) {
        this.boardStack = boardStack;
        this.groupNotification = groupNotification;
        this.gameBoardView = gameBoardView;
        this.palette = palette;
    }
    
    /**
     * Sets the row clear sound effect.
     * 
     * @param rowClearSound The audio clip to play when rows are cleared
     */
    public void setRowClearSound(AudioClip rowClearSound) {
        this.rowClearSound = rowClearSound;
    }
    
    /**
     * Animates cleared rows with visual effects and plays sound.
     * This is a visual-only animation that turns cleared row blocks into falling and vanishing rectangles.
     * This does not alter game state; it only animates an overlay based on the previous board snapshot.
     * 
     * @param prevMatrix The board matrix before rows were cleared
     * @param clearRow Information about which rows were cleared
     */
    public void animateClearedRows(int[][] prevMatrix, ClearRow clearRow) {
        if (prevMatrix == null || clearRow == null || clearRow.getLinesRemoved() <= 0) return;
        
        // Play sound effect
        if (rowClearSound != null) {
            rowClearSound.setVolume(GameSettings.getVolume() / 100.0);
            rowClearSound.play();
        }

        if (boardStack == null) return;
        if (!(gameBoardView instanceof JavaFxBoardView)) return;
        JavaFxBoardView jfxView = (JavaFxBoardView) gameBoardView;

        int[] rows = clearRow.getClearedRows();
        
        // Visual-only: small vertical shake on the board for multi-row clears (2 or more rows)
        if (clearRow.getLinesRemoved() >= 2 && boardStack != null) {
            animateBoardShake(clearRow.getLinesRemoved());
        }
        
        System.out.println("Animating cleared rows: " + rows.length + " rows");
        
        // Animate each cleared block falling and fading out
        for (int r : rows) {
            for (int c = 0; c < prevMatrix[0].length; c++) {
                int color = prevMatrix[r][c];
                if (color == 0) continue;
                
                animateSingleBlock(jfxView, r, c, color);
            }
        }
    }
    
    /**
     * Animates a shake effect on the board for multi-row clears.
     * 
     * @param linesRemoved The number of lines removed (determines shake amplitude)
     */
    private void animateBoardShake(int linesRemoved) {
        // Make a small amplitude based on lines removed: 2 -> 6px, 3 -> 8px, 4 -> 10px
        final double amplitude = 6.0 + Math.max(0, linesRemoved - 2) * 2.0;
        Platform.runLater(() -> {
            // Keyframe timeline creates a quick vertical shake (up / down / settle)
            Timeline shake = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(boardStack.translateYProperty(), 0)),
                    new KeyFrame(Duration.millis(40), new KeyValue(boardStack.translateYProperty(), -amplitude)),
                    new KeyFrame(Duration.millis(80), new KeyValue(boardStack.translateYProperty(), amplitude)),
                    new KeyFrame(Duration.millis(120), new KeyValue(boardStack.translateYProperty(), -amplitude / 2.0)),
                    new KeyFrame(Duration.millis(160), new KeyValue(boardStack.translateYProperty(), 0))
            );
            shake.setCycleCount(1);
            shake.play();
        });
    }
    
    /**
     * Animates a single cleared block with falling, fading, and rotation effects.
     * 
     * @param jfxView The JavaFX board view for position calculations
     * @param row The row index of the block
     * @param col The column index of the block
     * @param color The color index of the block
     */
    private void animateSingleBlock(JavaFxBoardView jfxView, int row, int col, int color) {
        Rectangle rect = new Rectangle(BRICK_SIZE, BRICK_SIZE);
        rect.setFill(palette.colorFor(color));
        rect.setArcHeight(0);
        rect.setArcWidth(0);
        rect.setMouseTransparent(true);
        
        // Compute position inside boardStack
        javafx.geometry.Point2D scenePoint = jfxView.getCellScenePosition(col, row);
        javafx.geometry.Point2D local;
        
        if (groupNotification != null) {
            local = groupNotification.sceneToLocal(scenePoint);
            // Skip cells above visible region (hidden rows)
            if (local.getY() < -8) {
                return;
            }
            // Position the rect absolutely within groupNotification so it's above everything
            rect.setLayoutX(local.getX());
            rect.setLayoutY(local.getY());
            groupNotification.getChildren().add(rect);
        } else if (boardStack != null) {
            local = boardStack.sceneToLocal(scenePoint);
            if (local.getY() < -8) {
                return;
            }
            StackPane.setAlignment(rect, javafx.geometry.Pos.TOP_LEFT);
            rect.setTranslateX(local.getX());
            rect.setTranslateY(local.getY());
            boardStack.getChildren().add(rect);
        } else {
            // No place to draw overlay; skip
            return;
        }

        // Animation: fall a longer amount and fade out with a slight stagger + horizontal spread and rotation
        // Shorten the durations and delays to make the clear animation quicker while preserving visuals
        TranslateTransition tt = new TranslateTransition(Duration.millis(350), rect);
        tt.setByY(BRICK_SIZE * 2.0); // longer fall
        
        // Small horizontal spread: random lateral byX to spread the falling bricks slightly
        double spreadPx = (animationRandom.nextDouble() - 0.5) * BRICK_SIZE * 1.2; // ±12px range
        tt.setByX(spreadPx);
        
        FadeTransition ft = new FadeTransition(Duration.millis(320), rect);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        
        long baseDelay = 40; // reduced base delay
        long stagger = (col * 14) + (row % 3) * 6; // lateral + small row-based offset (reduced)
        ft.setDelay(Duration.millis(baseDelay + stagger));
        tt.setDelay(Duration.millis(baseDelay + stagger));
        
        // Add a gentle rotation so pieces twist as they fall
        RotateTransition rt = new RotateTransition(Duration.millis(350), rect);
        rt.setByAngle((animationRandom.nextDouble() - 0.5) * 36.0); // -18 .. +18 deg
        rt.setDelay(Duration.millis(baseDelay + stagger));
        
        ParallelTransition pt = new ParallelTransition(tt, ft, rt);
        pt.setOnFinished(ev -> {
            try {
                if (boardStack != null) {
                    boardStack.getChildren().remove(rect);
                }
            } catch (Exception ignored) {
            }
        });
        pt.play();
    }
}
