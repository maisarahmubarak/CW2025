package com.comp2042.input;

import java.util.function.Consumer;

/**
 * Interface for a source of input events.
 * <p>
 * Defines how input events are bound to a consumer and how adapters are set.
 * </p>
 */
public interface InputSource {

    void bind(Consumer<InputEvent> consumer);

    void unbind();

    void setAdapter(InputAdapter<?> adapter);
}
