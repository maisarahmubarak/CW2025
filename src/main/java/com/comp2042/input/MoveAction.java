package com.comp2042.input;

/**
 * Represents a movement action in the game.
 * <p>
 * Encapsulates the type of action (e.g., DOWN, LEFT) and the source of the action (USER or THREAD).
 * </p>
 */
public final class MoveAction {
    private final ActionType eventType;
    private final ActionSource eventSource;

    /**
     * Constructs a new MoveAction.
     *
     * @param eventType   the type of action.
     * @param eventSource the source of the action.
     */
    public MoveAction(ActionType eventType, ActionSource eventSource) {
        this.eventType = eventType;
        this.eventSource = eventSource;
    }

    /**
     * Gets the type of action.
     *
     * @return the {@link ActionType}.
     */
    public ActionType getActionType() {
        return eventType;
    }

    /**
     * Deprecated compatibility getter. Use {@link #getActionType()}.
     *
     * @return the {@link ActionType}.
     */
    @Deprecated
    public ActionType getEventType() {
        return getActionType();
    }

    /**
     * Gets the source of the action.
     *
     * @return the {@link ActionSource}.
     */
    public ActionSource getEventSource() {
        return eventSource;
    }
}
