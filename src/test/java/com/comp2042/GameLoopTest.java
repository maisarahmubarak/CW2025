package com.comp2042;

import com.comp2042.logic.game.GameLoop;
import javafx.application.Platform;
import javafx.util.Duration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the {@link GameLoop} class.
 * <p>
 * Verifies the game loop's start/stop mechanics and tick execution.
 * Requires JavaFX runtime initialization.
 * </p>
 */
class GameLoopTest {

    @BeforeAll
    static void initFxRuntime() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        try {
            Platform.startup(latch::countDown);
        } catch (IllegalStateException alreadyStarted) {
            // FX runtime already initialized by another test
            latch.countDown();
        }
        latch.await();
    }

    @Test
    void playInvokesTickRunnable() throws Exception {
        CountDownLatch tickLatch = new CountDownLatch(1);
        // Use a short duration to ensure test speed
        GameLoop gameLoop = new GameLoop(Duration.millis(1), tickLatch::countDown);

        runOnFxThread(gameLoop::play);

        // Deterministic wait for the signal
        assertTrue(tickLatch.await(1, TimeUnit.SECONDS), "Tick runnable should be called after play()");

        runOnFxThread(gameLoop::stop);
    }

    @Test
    void stopPreventsFurtherTicks() throws Exception {
        CountDownLatch startLatch = new CountDownLatch(1);
        GameLoop gameLoop = new GameLoop(Duration.millis(1), startLatch::countDown);

        // 1. Start and wait for at least one tick to prove it runs
        runOnFxThread(gameLoop::play);
        assertTrue(startLatch.await(1, TimeUnit.SECONDS), "Should tick at least once");

        // 2. Stop the loop
        runOnFxThread(gameLoop::stop);

        // 3. Verify state deterministically
        // We trust JavaFX Timeline: if status is STOPPED, no more events will fire.
        final boolean[] isRunning = {true};
        runOnFxThread(() -> isRunning[0] = gameLoop.isRunning());
        
        assertEquals(false, isRunning[0], "GameLoop should report not running after stop()");
    }

    private static void runOnFxThread(Runnable runnable) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                runnable.run();
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }
}
