package com.comp2042;

import com.comp2042.logic.board.ClearRow;
import com.comp2042.logic.board.MatrixOperations;
import com.comp2042.ui.GameBoardView;
import com.comp2042.ui.GuiController;
import com.comp2042.ui.GameViewController;
import com.comp2042.ui.effects.BoardAnimationController;
import javafx.application.Platform;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

public class GuiControllerAnimationTest {

    @Test
    public void animateClearedRowsAddsHighlightNodesAndCleansUp() throws Exception {
        // Create a GUI context
        GridPane gamePanel = new GridPane();
        GridPane brickPanel = new GridPane();
        GridPane previewPanel = new GridPane();
        StackPane boardStack = new StackPane();

        final GuiController controller = new GuiController();
        
        // Initialize a JavaFxBoardView
        GameBoardView jfxView = new GameBoardView(gamePanel, brickPanel, previewPanel, 20);
        
        // Initialize BoardAnimationController
        BoardAnimationController animationController = new BoardAnimationController(
            boardStack, 
            null, // groupNotification not needed for this test
            jfxView, 
            new ClassicBrickPalette()
        );

        // Initialize GameViewController with the animation controller
        GameViewController gameViewController = new GameViewController(
            gamePanel,
            null, // groupNotification
            null, // gameLifecycleController
            null, // gameInputController
            animationController,
            null, // scoreUiController
            jfxView
        );

        // Inject GameViewController into GuiController
        controller.setGameViewController(gameViewController);

        // Setup a sample prev matrix with a full bottom row
        int rows = 28;
        int cols = 10;
        int[][] prevMatrix = new int[rows][cols];
        int fullRow = rows - 1; // bottom row
        for (int c = 0; c < cols; c++) {
            prevMatrix[fullRow][c] = 1; // Color 1 (Cyan)
        }
        int[] clearedRows = new int[]{fullRow};
        ClearRow cr = new ClearRow(1, MatrixOperations.copy(prevMatrix), 50, clearedRows);

        // Ensure JavaFX toolkit is initialized (Platform.startup can only be called once)
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException ignored) {
            // toolkit already initialized
        }
        
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch finishLatch = new CountDownLatch(1);
        AtomicReference<Throwable> errorRef = new AtomicReference<>();

        Platform.runLater(() -> {
            try {
                controller.animateClearedRows(prevMatrix, cr);
                
                // ASSERTION 1: Verify 10 rectangles are added (one for each block in the cleared row)
                int childCount = boardStack.getChildren().size();
                if (childCount != 10) {
                    throw new AssertionError("Expected 10 animation nodes, found " + childCount);
                }
                
                // ASSERTION 2: Verify nodes are Rectangles with correct color
                for (javafx.scene.Node node : boardStack.getChildren()) {
                    if (!(node instanceof Rectangle)) {
                        throw new AssertionError("Animation node should be a Rectangle");
                    }
                    Rectangle rect = (Rectangle) node;
                    // Color 1 in ClassicBrickPalette is #00E5FF
                    if (!rect.getFill().equals(Color.web("#00E5FF"))) {
                         throw new AssertionError("Rectangle should be Cyan (#00E5FF) for brick type 1");
                    }
                }
                
                startLatch.countDown();

                // Schedule cleanup check
                new Thread(() -> {
                    try {
                        // Wait longer than animation duration (~530ms)
                        Thread.sleep(1000);
                        Platform.runLater(() -> {
                            try {
                                // ASSERTION 3: Verify nodes are removed after animation
                                int finalCount = boardStack.getChildren().size();
                                if (finalCount != 0) {
                                    throw new AssertionError("Expected 0 nodes after animation, found " + finalCount);
                                }
                                finishLatch.countDown();
                            } catch (Throwable t) {
                                errorRef.set(t);
                                finishLatch.countDown();
                            }
                        });
                    } catch (InterruptedException ignored) {}
                }).start();

            } catch (Throwable t) {
                errorRef.set(t);
                startLatch.countDown();
                finishLatch.countDown();
            }
        });

        assertTrue(startLatch.await(2, TimeUnit.SECONDS), "Animation start timed out");
        if (errorRef.get() != null) {
             throw new RuntimeException("Assertion failed during animation start", errorRef.get());
        }

        assertTrue(finishLatch.await(3, TimeUnit.SECONDS), "Animation finish timed out");
        if (errorRef.get() != null) {
            throw new RuntimeException("Assertion failed during animation cleanup", errorRef.get());
        }
    }
}
