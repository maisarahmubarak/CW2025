package com.comp2042;

import com.comp2042.input.ActionSource;
import com.comp2042.input.ActionType;
import com.comp2042.input.InputEvent;
import com.comp2042.input.KeyEventInputAdapter;
import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

class KeyEventInputAdapterTest {

    @BeforeAll
    static void ensureFxRuntime() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        try {
            Platform.startup(latch::countDown);
        } catch (IllegalStateException alreadyStarted) {
            latch.countDown();
        }
        latch.await();
    }

    @Test
    void translateLeftArrowProducesMoveEvent() {
        KeyEventInputAdapter adapter = new KeyEventInputAdapter();

        Optional<InputEvent> result = adapter.translate(keyPress(KeyCode.LEFT));

        assertTrue(result.isPresent());
        assertEquals(InputEvent.Kind.MOVE, result.get().getKind());
        assertEquals(ActionType.LEFT, result.get().getMoveAction().getActionType());
        assertEquals(ActionSource.USER, result.get().getMoveAction().getEventSource());
    }

    @Test
    void translateNKeyCreatesNewGameEvent() {
        KeyEventInputAdapter adapter = new KeyEventInputAdapter();

        Optional<InputEvent> result = adapter.translate(keyPress(KeyCode.N));

        assertTrue(result.isPresent());
        assertEquals(InputEvent.Kind.NEW_GAME, result.get().getKind());
        assertNull(result.get().getMoveAction());
    }

    @Test
    void unrelatedKeysReturnEmptyOptional() {
        KeyEventInputAdapter adapter = new KeyEventInputAdapter();

        Optional<InputEvent> result = adapter.translate(keyPress(KeyCode.SPACE));

        assertTrue(result.isEmpty());
    }

    private static KeyEvent keyPress(KeyCode code) {
        return new KeyEvent(KeyEvent.KEY_PRESSED, "", "", code, false, false, false, false);
    }
}
