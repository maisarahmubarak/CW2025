package com.comp2042;

public final class MoveAction {
    private final EventType eventType;
    private final ActionSource eventSource;

    public MoveAction(EventType eventType, ActionSource eventSource) {
        this.eventType = eventType;
        this.eventSource = eventSource;
    }

    public EventType getEventType() {
        return eventType;
    }

    public ActionSource getEventSource() {
        return eventSource;
    }
}
