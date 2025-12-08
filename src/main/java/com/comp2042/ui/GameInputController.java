package com.comp2042.ui;

import com.comp2042.input.ActionSource;
import com.comp2042.input.ActionType;
import com.comp2042.input.InputActionListener;
import com.comp2042.input.MoveAction;
import com.comp2042.ui.state.GameLifecycleController;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * GameInputController handles all keyboard input logic for the game.
 * This class is responsible for:
 * - Mapping keyboard keys to game actions (pause, new game, movement, rotation, soft drop)
 * - Checking game state (pause, game over) before allowing movement
 * - Handling control flip logic in Danger mode
 * - Dispatching actions to appropriate controllers and listeners
 */
public class GameInputController {
    
    private GameLifecycleController gameLifecycleController;
    private GameBoardView gameBoardView;
    private InputActionListener eventListener;
    private Runnable moveDownCallback;
    
    /**
     * Constructor for GameInputController.
     * 
     * @param gameLifecycleController The game lifecycle controller for state checks and game control actions
     * @param gameBoardView The game board view for updating the display after actions
     * @param eventListener The input action listener for handling movement events
     * @param moveDownCallback The callback for triggering soft drop (DOWN key)
     */
    /**
     * Constructs a new GameInputController.
     * <p>
     * Initializes the controller with the necessary dependencies to handle game input.
     * </p>
     * 
     * @param gameLifecycleController The game lifecycle controller for state checks and game control actions.
     * @param gameBoardView The game board view for updating the display after actions.
     * @param eventListener The input action listener for handling movement events.
     * @param moveDownCallback The callback for triggering soft drop (DOWN key).
     */
    public GameInputController(GameLifecycleController gameLifecycleController, 
                               GameBoardView gameBoardView,
                               InputActionListener eventListener,
                               Runnable moveDownCallback) {
        this.gameLifecycleController = gameLifecycleController;
        this.gameBoardView = gameBoardView;
        this.eventListener = eventListener;
        this.moveDownCallback = moveDownCallback;
    }
    
    /**
     * Sets the game lifecycle controller.
     * <p>
     * Updates the reference to the lifecycle controller used for checking game state.
     * </p>
     *
     * @param gameLifecycleController The GameLifecycleController to set.
     */
    public void setGameLifecycleController(GameLifecycleController gameLifecycleController) {
        this.gameLifecycleController = gameLifecycleController;
    }
    
    /**
     * Sets the game board view.
     * <p>
     * Updates the reference to the view component used for refreshing the display.
     * </p>
     *
     * @param gameBoardView The GameBoardView to set.
     */
    public void setGameBoardView(GameBoardView gameBoardView) {
        this.gameBoardView = gameBoardView;
    }
    
    /**
     * Sets the input action listener.
     * <p>
     * Updates the listener that processes game logic events (move, rotate).
     * </p>
     *
     * @param eventListener The InputActionListener to set.
     */
    public void setEventListener(InputActionListener eventListener) {
        this.eventListener = eventListener;
    }
    
    /**
     * Sets the move down callback.
     * <p>
     * Updates the runnable that is executed when the soft drop key is pressed.
     * </p>
     *
     * @param moveDownCallback The Runnable to set.
     */
    public void setMoveDownCallback(Runnable moveDownCallback) {
        this.moveDownCallback = moveDownCallback;
    }
    
    /**
     * Handles keyboard input events and dispatches them to appropriate actions.
     * <p>
     * Processes key presses for game controls. It checks the current game state (paused, game over)
     * before allowing movement. It also handles control flipping if enabled (e.g., in Danger mode).
     * </p>
     * 
     * <p>Key mappings:</p>
     * <ul>
     *   <li><b>P</b>: Toggle pause/resume</li>
     *   <li><b>N</b>: Start new game</li>
     *   <li><b>LEFT / A</b>: Move left (or right if controls are flipped)</li>
     *   <li><b>RIGHT / D</b>: Move right (or left if controls are flipped)</li>
     *   <li><b>UP / W</b>: Rotate piece</li>
     *   <li><b>DOWN / S</b>: Soft drop (move down faster)</li>
     * </ul>
     * 
     * @param keyEvent The keyboard event to handle.
     */
    public void handleKeyEvent(KeyEvent keyEvent) {
        // Handle pause toggle (P key) - always available
        if (keyEvent.getCode() == KeyCode.P) {
            gameLifecycleController.togglePause();
            keyEvent.consume();
            return;
        }
        
        // Handle movement and rotation - only when game is active (not paused or game over)
        if (gameLifecycleController.isPauseProperty().getValue() == Boolean.FALSE 
                && gameLifecycleController.isGameOverProperty().getValue() == Boolean.FALSE) {
            
            // Handle LEFT movement (or RIGHT if controls are flipped)
            if (keyEvent.getCode() == KeyCode.LEFT || keyEvent.getCode() == KeyCode.A) {
                if (gameLifecycleController.isControlsFlipped()) {
                    // Controls are flipped - LEFT key moves RIGHT
                    gameBoardView.refreshBrick(
                        eventListener.onRightEvent(new MoveAction(ActionType.RIGHT, ActionSource.USER))
                    );
                } else {
                    // Normal controls - LEFT key moves LEFT
                    gameBoardView.refreshBrick(
                        eventListener.onLeftEvent(new MoveAction(ActionType.LEFT, ActionSource.USER))
                    );
                }
                keyEvent.consume();
            }
            
            // Handle RIGHT movement (or LEFT if controls are flipped)
            if (keyEvent.getCode() == KeyCode.RIGHT || keyEvent.getCode() == KeyCode.D) {
                if (gameLifecycleController.isControlsFlipped()) {
                    // Controls are flipped - RIGHT key moves LEFT
                    gameBoardView.refreshBrick(
                        eventListener.onLeftEvent(new MoveAction(ActionType.LEFT, ActionSource.USER))
                    );
                } else {
                    // Normal controls - RIGHT key moves RIGHT
                    gameBoardView.refreshBrick(
                        eventListener.onRightEvent(new MoveAction(ActionType.RIGHT, ActionSource.USER))
                    );
                }
                keyEvent.consume();
            }
            
            // Handle ROTATE (UP or W key)
            if (keyEvent.getCode() == KeyCode.UP || keyEvent.getCode() == KeyCode.W) {
                gameBoardView.refreshBrick(
                    eventListener.onRotateEvent(new MoveAction(ActionType.ROTATE, ActionSource.USER))
                );
                keyEvent.consume();
            }
            
            // Handle SOFT DROP (DOWN or S key)
            if (keyEvent.getCode() == KeyCode.DOWN || keyEvent.getCode() == KeyCode.S) {
                if (moveDownCallback != null) {
                    moveDownCallback.run();
                }
                keyEvent.consume();
            }
        }
        
        // Handle NEW GAME (N key) - always available
        if (keyEvent.getCode() == KeyCode.N) {
            gameLifecycleController.newGame(null);
        }
    }
}
