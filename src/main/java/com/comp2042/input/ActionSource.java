package com.comp2042.input;

/**
 * Enumeration representing the source of an action or event.
 * <p>
 * Used to distinguish between actions initiated by the user (e.g., key presses)
 * and actions initiated by the game thread (e.g., automatic gravity).
 * </p>
 */
public enum ActionSource {
    /**
     * Action initiated by the user.
     */
    USER,
    /**
     * Action initiated by the game thread.
     */
    THREAD
}
