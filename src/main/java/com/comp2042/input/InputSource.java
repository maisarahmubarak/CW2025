package com.comp2042.input;

import java.util.function.Consumer;

/**
 * Interface for input event sources.
 * <p>
 * Defines how input events are bound to a consumer and how adapters are set.
 * </p>
 *
 * @author Maisarah
 * @version 1.0
 */
public interface InputSource {

    void bind(Consumer<InputEvent> consumer);

    void unbind();

    void setAdapter(InputAdapter<?> adapter);
}
