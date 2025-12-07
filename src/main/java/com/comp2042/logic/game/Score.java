package com.comp2042.logic.game;

import javafx.beans.property.IntegerProperty;

/**
 * Deprecated wrapper kept for backward compatibility. Use {@link GameScore} instead.
 */
@Deprecated
public final class Score {

    private final GameScore delegate = new GameScore();

    public IntegerProperty scoreProperty() {
        return delegate.scoreProperty();
    }

    public void add(int i) {
        delegate.add(i);
    }

    public void reset() {
        delegate.reset();
    }
}