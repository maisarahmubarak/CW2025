package com.comp2042.input;

import java.util.function.Consumer;

public interface InputSource {

    void bind(Consumer<InputEvent> consumer);

    void unbind();

    void setAdapter(InputAdapter<?> adapter);
}
