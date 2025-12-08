package com.comp2042;

import com.comp2042.input.ActionSource;
import com.comp2042.input.ActionType;
import com.comp2042.input.InputEvent;
import com.comp2042.input.MoveAction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link InputEvent} class.
 * <p>
 * Verifies the creation and properties of input events (moves, new game).
 * </p>
 */
class InputEventTest {

    /**
     * Verifies that the factory method creates a MOVE event with the correct action type and source.
     */
    @Test
    void moveFactoryCreatesMoveEventWithAction() {
        MoveAction action = new MoveAction(ActionType.LEFT, ActionSource.USER);

        InputEvent event = InputEvent.move(action);

        assertEquals(InputEvent.Kind.MOVE, event.getKind());
        assertSame(action, event.getMoveAction());
    }

    @Test
    void newGameFactoryCreatesEventWithoutMoveAction() {
        InputEvent event = InputEvent.newGame();

        assertEquals(InputEvent.Kind.NEW_GAME, event.getKind());
        assertNull(event.getMoveAction());
    }
}
