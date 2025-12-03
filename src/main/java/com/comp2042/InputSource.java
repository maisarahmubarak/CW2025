package com.comp2042;

import java.util.function.Consumer;

public interface InputSource {

    void bind(Consumer<InputEvent> consumer);

    void unbind();

    void setAdapter(InputAdapter<?> adapter);
}
