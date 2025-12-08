package com.comp2042.input;

/**
 * Represents a translated game command coming from any input adapter.
 */
/**
 * Represents a translated game command coming from any input adapter.
 */
public class InputEvent {

    /**
     * The kind of input event.
     */
    public enum Kind {
        /**
         * Represents a movement action (left, right, down, rotate).
         */
        MOVE,
        /**
         * Represents a request to start a new game.
         */
        NEW_GAME
    }

    private final Kind kind;
    private final MoveAction moveAction;

    private InputEvent(Kind kind, MoveAction moveAction) {
        this.kind = kind;
        this.moveAction = moveAction;
    }

    /**
     * Creates a new MOVE event.
     *
     * @param moveAction the specific move action details.
     * @return a new InputEvent of kind MOVE.
     */
    public static InputEvent move(MoveAction moveAction) {
        return new InputEvent(Kind.MOVE, moveAction);
    }

    /**
     * Creates a new NEW_GAME event.
     *
     * @return a new InputEvent of kind NEW_GAME.
     */
    public static InputEvent newGame() {
        return new InputEvent(Kind.NEW_GAME, null);
    }

    /**
     * Gets the kind of this event.
     *
     * @return the {@link Kind} of event.
     */
    public Kind getKind() {
        return kind;
    }

    /**
     * Gets the move action associated with this event.
     *
     * @return the {@link MoveAction}, or null if this is not a MOVE event.
     */
    public MoveAction getMoveAction() {
        return moveAction;
    }
}
