package com.comp2042.input;

import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;

import java.util.Optional;
import java.util.function.Consumer;

public class JavaFxInputSource implements InputSource {

    private final Pane inputPane;
    private final EventHandler<KeyEvent> handler = this::handleEvent;
    private InputAdapter<KeyEvent> adapter;
    private Consumer<InputEvent> consumer;

    public JavaFxInputSource(Pane inputPane, InputAdapter<KeyEvent> adapter) {
        this.inputPane = inputPane;
        this.adapter = adapter;
    }

    @Override
    public void bind(Consumer<InputEvent> consumer) {
        this.consumer = consumer;
        inputPane.setOnKeyPressed(handler);
    }

    @Override
    public void unbind() {
        consumer = null;
        inputPane.setOnKeyPressed(null);
    }

    @Override
    public void setAdapter(InputAdapter<?> adapter) {
        if (adapter instanceof InputAdapter) {
            @SuppressWarnings("unchecked")
            InputAdapter<KeyEvent> keyAdapter = (InputAdapter<KeyEvent>) adapter;
            this.adapter = keyAdapter;
        }
    }

    private void handleEvent(KeyEvent event) {
        if (adapter == null || consumer == null) {
            return;
        }
        Optional<InputEvent> translated = adapter.translate(event);
        translated.ifPresent(consumer);
        if (translated.isPresent()) {
            event.consume();
        }
    }
}
