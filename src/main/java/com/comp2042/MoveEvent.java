package com.comp2042;

/**
 * Deprecated wrapper for compatibility. Use {@link MoveAction} instead.
 */
@Deprecated
public final class MoveEvent {
    private final MoveAction delegate;

    public MoveEvent(EventType eventType, EventSource eventSource) {
        this.delegate = new MoveAction(eventType, eventSource);
    }

    public EventType getEventType() {
        return delegate.getEventType();
    }

    public EventSource getEventSource() {
        return delegate.getEventSource();
    }

    public MoveAction toMoveAction() {
        return delegate;
    }
}
