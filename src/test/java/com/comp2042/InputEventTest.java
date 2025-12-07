package com.comp2042;

import com.comp2042.input.ActionSource;
import com.comp2042.input.ActionType;
import com.comp2042.input.InputEvent;
import com.comp2042.input.MoveAction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InputEventTest {

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
