package com.comp2042.input;

/**
 * Interface for action event sources.
 * <p>
 * Used to distinguish between actions initiated by the user (e.g., key presses)
 * and actions initiated by the game thread (e.g., automatic gravity).
 * </p>
 *
 * @author Maisarah
 * @version 1.0
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
