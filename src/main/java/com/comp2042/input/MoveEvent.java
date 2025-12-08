package com.comp2042.input;

/**
 * Deprecated wrapper for compatibility. Use {@link MoveAction} instead.
 */
@Deprecated
public final class MoveEvent {
    private final MoveAction delegate;

    /**
     * Constructs a new MoveEvent.
     *
     * @param eventType the type of action.
     * @param eventSource the source of the action.
     */
    public MoveEvent(ActionType eventType, ActionSource eventSource) {
        this.delegate = new MoveAction(eventType, eventSource);
    }

    /**
     * New name for the getter returning the action type.
     *
     * @return the {@link ActionType}.
     */
    public ActionType getActionType() {
        return delegate.getActionType();
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
        return delegate.getEventSource();
    }

    /**
     * Converts this legacy event to the new {@link MoveAction} type.
     *
     * @return the underlying {@link MoveAction}.
     */
    public MoveAction toMoveAction() {
        return delegate;
    }
}
