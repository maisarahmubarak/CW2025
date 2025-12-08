package com.comp2042.input;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

/**
 * Adapter converting raw input to game actions.
 * <p>
 * Converts JavaFX KeyEvents into domain-level input events so the controller
 * never needs to inspect framework-specific details.
 * </p>
 *
 * @author Maisarah
 * @version 1.0
 */
public class KeyEventInputAdapter implements InputAdapter<KeyEvent> {

    private static final Set<KeyCode> LEFT = EnumSet.of(KeyCode.LEFT, KeyCode.A);
    private static final Set<KeyCode> RIGHT = EnumSet.of(KeyCode.RIGHT, KeyCode.D);
    private static final Set<KeyCode> DOWN = EnumSet.of(KeyCode.DOWN, KeyCode.S);
    private static final Set<KeyCode> ROTATE = EnumSet.of(KeyCode.UP, KeyCode.W);

    /**
     * Translates a JavaFX KeyEvent into an optional InputEvent.
     *
     * @param event the JavaFX KeyEvent to translate.
     * @return an {@link Optional} containing the translated {@link InputEvent}, or empty if the key is not mapped.
     */
    @Override
    public Optional<InputEvent> translate(KeyEvent event) {
        KeyCode code = event.getCode();
        if (LEFT.contains(code)) {
            return Optional.of(InputEvent.move(new MoveAction(ActionType.LEFT, ActionSource.USER)));
        }
        if (RIGHT.contains(code)) {
            return Optional.of(InputEvent.move(new MoveAction(ActionType.RIGHT, ActionSource.USER)));
        }
        if (DOWN.contains(code)) {
            return Optional.of(InputEvent.move(new MoveAction(ActionType.DOWN, ActionSource.USER)));
        }
        if (ROTATE.contains(code)) {
            return Optional.of(InputEvent.move(new MoveAction(ActionType.ROTATE, ActionSource.USER)));
        }
        if (code == KeyCode.N) {
            return Optional.of(InputEvent.newGame());
        }
        return Optional.empty();
    }
}
