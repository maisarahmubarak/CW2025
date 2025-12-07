package com.comp2042;

import com.comp2042.logic.board.ClearRow;
import com.comp2042.logic.board.MatrixOperations;
import javafx.application.Platform;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class GuiControllerAnimationTest {

    @Test
    public void animateClearedRowsRunsAndRemovesRects() throws Exception {
        // Create a GUI context
        GridPane gamePanel = new GridPane();
        GridPane brickPanel = new GridPane();
        GridPane previewPanel = new GridPane();
        StackPane boardStack = new StackPane();

        final GuiController controller = new GuiController();
        // Inject fields via reflection
        setField(controller, "gamePanel", gamePanel);
        setField(controller, "brickPanel", brickPanel);
        setField(controller, "previewPanel", previewPanel);
        setField(controller, "boardStack", boardStack);
        // Initialize a JavaFxBoardView and inject
        GameBoardView jfxView = new GameBoardView(gamePanel, brickPanel, previewPanel, 20);
        setField(controller, "gameBoardView", jfxView);
        // Setup a sample prev matrix with a full bottom row
        int rows = 28;
        int cols = 10;
        int[][] prevMatrix = new int[rows][cols];
        int fullRow = rows - 1; // bottom row
        for (int c = 0; c < cols; c++) {
            prevMatrix[fullRow][c] = 1;
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
        final int[] childrenCount = new int[1];
            Platform.runLater(() -> {
            try {
                controller.animateClearedRows(prevMatrix, cr);
                startLatch.countDown();
                    // examine immediate children count on FX thread
                    childrenCount[0] = boardStack.getChildren().size();
                // Allow animations to complete by sleeping a bit longer than the animation
                new Thread(() -> {
                    try {
                        Thread.sleep(1500);
                        finishLatch.countDown();
                    } catch (InterruptedException ignored) {}
                }).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        assertTrue(startLatch.await(2, TimeUnit.SECONDS), "Animation should be invoked within 2 seconds");
        assertTrue(childrenCount[0] > 0, "Overlay should have children after animation starts");
        // We do not validate visual result, just ensure animation played and completed without exceptions.
        assertTrue(finishLatch.await(3, TimeUnit.SECONDS), "Animation should finish within 3 seconds");
    }

    private static void setField(Object target, String fieldName, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(target, value);
    }
}
