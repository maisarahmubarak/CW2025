package com.comp2042;

/**
 * Deprecated wrapper for compatibility. Use {@link MoveAction} instead.
 */
@Deprecated
public final class MoveEvent {
    private final MoveAction delegate;

    public MoveEvent(ActionType eventType, ActionSource eventSource) {
        this.delegate = new MoveAction(eventType, eventSource);
    }

    /**
     * New name for the getter returning the action type.
     */
    public ActionType getActionType() {
        return delegate.getActionType();
    }

    /**
     * Deprecated compatibility getter. Use {@link #getActionType()}.
     */
    @Deprecated
    public ActionType getEventType() {
        return getActionType();
    }

    public ActionSource getEventSource() {
        return delegate.getEventSource();
    }

    public MoveAction toMoveAction() {
        return delegate;
    }
}
