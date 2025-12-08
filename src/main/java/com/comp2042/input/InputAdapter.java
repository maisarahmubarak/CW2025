package com.comp2042.input;

import java.util.Optional;

/**
 * Adapter converting raw input to game actions.
 * <p>
 * Translates UI/input framework events into domain-level commands the game understands.
 * </p>
 *
 * @author Maisarah
 * @version 1.0
 */
public interface InputAdapter<T> {

    /**
     * Attempt to translate the low-level event into a game input. Empty when the
     * event is irrelevant for gameplay.
     */
    Optional<InputEvent> translate(T event);
}
