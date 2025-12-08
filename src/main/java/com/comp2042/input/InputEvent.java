package com.comp2042.input;

/**
 * Represents a translated game command coming from any input adapter.
 */
public class InputEvent {

    public enum Kind {
        MOVE,
        NEW_GAME
    }

    private final Kind kind;
    private final MoveAction moveAction;

    private InputEvent(Kind kind, MoveAction moveAction) {
        this.kind = kind;
        this.moveAction = moveAction;
    }

    public static InputEvent move(MoveAction moveAction) {
        return new InputEvent(Kind.MOVE, moveAction);
    }

    public static InputEvent newGame() {
        return new InputEvent(Kind.NEW_GAME, null);
    }

    public Kind getKind() {
        return kind;
    }

    public MoveAction getMoveAction() {
        return moveAction;
    }
}
