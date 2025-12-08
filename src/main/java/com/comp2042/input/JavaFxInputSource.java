package com.comp2042.input;

import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * JavaFX-specific input implementation.
 * <p>
 * Captures JavaFX KeyEvents from a Pane and translates them into game {@link InputEvent}s.
 * Binds key events from a JavaFX {@link Pane} to the game's input system.
 * </p>
 *
 * @author Maisarah
 * @version 1.0
 */
public class JavaFxInputSource implements InputSource {

    private final Pane inputPane;
    private final EventHandler<KeyEvent> handler = this::handleEvent;
    private InputAdapter<KeyEvent> adapter;
    private Consumer<InputEvent> consumer;

    /**
     * Constructs a new JavaFxInputSource.
     *
     * @param inputPane the JavaFX Pane to listen for key events on.
     * @param adapter the adapter to translate KeyEvents into InputEvents.
     */
    public JavaFxInputSource(Pane inputPane, InputAdapter<KeyEvent> adapter) {
        this.inputPane = inputPane;
        this.adapter = adapter;
    }

    /**
     * Binds the input source to a consumer.
     * <p>
     * Starts listening for key events on the configured pane.
     * </p>
     *
     * @param consumer the consumer that will receive generated InputEvents.
     */
    @Override
    public void bind(Consumer<InputEvent> consumer) {
        this.consumer = consumer;
        inputPane.setOnKeyPressed(handler);
    }

    /**
     * Unbinds the input source.
     * <p>
     * Stops listening for key events.
     * </p>
     */
    @Override
    public void unbind() {
        consumer = null;
        inputPane.setOnKeyPressed(null);
    }

    /**
     * Sets the input adapter.
     *
     * @param adapter the new adapter to use for translation.
     */
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
