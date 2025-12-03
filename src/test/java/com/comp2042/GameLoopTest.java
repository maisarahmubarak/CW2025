package com.comp2042;

import javafx.application.Platform;
import javafx.util.Duration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        GameLoop gameLoop = new GameLoop(Duration.millis(20), tickLatch::countDown);

        runOnFxThread(gameLoop::play);

        assertTrue(tickLatch.await(1, TimeUnit.SECONDS), "Tick runnable should be called after play()");

        runOnFxThread(gameLoop::stop);
    }

    @Test
    void stopPreventsFurtherTicks() throws Exception {
        AtomicInteger counter = new AtomicInteger();
        GameLoop gameLoop = new GameLoop(Duration.millis(15), counter::incrementAndGet);

        runOnFxThread(gameLoop::play);
        waitForCounter(counter, 2);
        runOnFxThread(gameLoop::stop);
        int countAfterStop = counter.get();
        Thread.sleep(100);

        assertEquals(countAfterStop, counter.get(), "Counter should remain constant after stop()");
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

    private static void waitForCounter(AtomicInteger counter, int target) throws InterruptedException {
        long deadline = System.currentTimeMillis() + 1000;
        while (counter.get() < target && System.currentTimeMillis() < deadline) {
            Thread.sleep(10);
        }
    }
}
