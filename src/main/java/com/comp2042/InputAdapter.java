package com.comp2042;

import java.util.Optional;

/**
 * Adapter abstraction that translates UI/input framework events into
 * domain-level commands the game understands.
 */
public interface InputAdapter<T> {

    /**
     * Attempt to translate the low-level event into a game input. Empty when the
     * event is irrelevant for gameplay.
     */
    Optional<InputEvent> translate(T event);
}
