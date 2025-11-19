package com.comp2042;

public final class MoveAction {
    private final ActionType eventType;
    private final ActionSource eventSource;

    public MoveAction(ActionType eventType, ActionSource eventSource) {
        this.eventType = eventType;
        this.eventSource = eventSource;
    }

    public ActionType getEventType() {
        return eventType;
    }

    public ActionSource getEventSource() {
        return eventSource;
    }
}
