package com.comp2042.input;

public final class MoveAction {
    private final ActionType eventType;
    private final ActionSource eventSource;

    public MoveAction(ActionType eventType, ActionSource eventSource) {
        this.eventType = eventType;
        this.eventSource = eventSource;
    }

    /**
     * New name for the getter returning the action type.
     */
    public ActionType getActionType() {
        return eventType;
    }

    /**
     * Deprecated compatibility getter. Use {@link #getActionType()}.
     */
    @Deprecated
    public ActionType getEventType() {
        return getActionType();
    }

    public ActionSource getEventSource() {
        return eventSource;
    }
}
