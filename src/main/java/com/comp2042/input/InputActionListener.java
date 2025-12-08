package com.comp2042.input;

import com.comp2042.logic.game.DownData;
import com.comp2042.logic.game.ViewData;

/**
 * Interface for listening to and handling game input actions.
 * <p>
 * Implementations of this interface define how the game responds to various move actions
 * (down, left, right, rotate) and game control actions (create new game).
 * </p>
 */
public interface InputActionListener {

    /**
     * Called when a "down" move action occurs.
     *
     * @param event the {@link MoveAction} event.
     * @return a {@link DownData} object containing the result of the move.
     */
    DownData onDownEvent(MoveAction event);

    /**
     * Called when a "left" move action occurs.
     *
     * @param event the {@link MoveAction} event.
     * @return a {@link ViewData} object containing the updated view state.
     */
    ViewData onLeftEvent(MoveAction event);

    /**
     * Called when a "right" move action occurs.
     *
     * @param event the {@link MoveAction} event.
     * @return a {@link ViewData} object containing the updated view state.
     */
    ViewData onRightEvent(MoveAction event);

    /**
     * Called when a "rotate" move action occurs.
     *
     * @param event the {@link MoveAction} event.
     * @return a {@link ViewData} object containing the updated view state.
     */
    ViewData onRotateEvent(MoveAction event);

    /**
     * Called to start a new game.
     */
    void createNewGame();
}
