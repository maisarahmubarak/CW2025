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

    public ActionType getEventType() {
        return delegate.getEventType();
    }

    public ActionSource getEventSource() {
        return delegate.getEventSource();
    }

    public MoveAction toMoveAction() {
        return delegate;
    }
}
