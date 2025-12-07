package com.comp2042.ui;

import com.comp2042.input.ActionSource;
import com.comp2042.input.ActionType;
import com.comp2042.input.InputActionListener;
import com.comp2042.input.MoveAction;
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
    
    private GameStateController gameStateController;
    private GameBoardView gameBoardView;
    private InputActionListener eventListener;
    private Runnable moveDownCallback;
    
    /**
     * Constructor for GameInputController.
     * 
     * @param gameStateController The game state controller for state checks and game control actions
     * @param gameBoardView The game board view for updating the display after actions
     * @param eventListener The input action listener for handling movement events
     * @param moveDownCallback The callback for triggering soft drop (DOWN key)
     */
    public GameInputController(GameStateController gameStateController, 
                               GameBoardView gameBoardView,
                               InputActionListener eventListener,
                               Runnable moveDownCallback) {
        this.gameStateController = gameStateController;
        this.gameBoardView = gameBoardView;
        this.eventListener = eventListener;
        this.moveDownCallback = moveDownCallback;
    }
    
    /**
     * Sets the game state controller.
     */
    public void setGameStateController(GameStateController gameStateController) {
        this.gameStateController = gameStateController;
    }
    
    /**
     * Sets the game board view.
     */
    public void setGameBoardView(GameBoardView gameBoardView) {
        this.gameBoardView = gameBoardView;
    }
    
    /**
     * Sets the input action listener.
     */
    public void setEventListener(InputActionListener eventListener) {
        this.eventListener = eventListener;
    }
    
    /**
     * Sets the move down callback.
     */
    public void setMoveDownCallback(Runnable moveDownCallback) {
        this.moveDownCallback = moveDownCallback;
    }
    
    /**
     * Handles keyboard input events and dispatches them to appropriate actions.
     * 
     * Key mappings:
     * - P: Toggle pause/resume
     * - N: Start new game
     * - LEFT/A: Move left (or right if controls are flipped)
     * - RIGHT/D: Move right (or left if controls are flipped)
     * - UP/W: Rotate piece
     * - DOWN/S: Soft drop (move down faster)
     * 
     * @param keyEvent The keyboard event to handle
     */
    public void handleKeyEvent(KeyEvent keyEvent) {
        // Handle pause toggle (P key) - always available
        if (keyEvent.getCode() == KeyCode.P) {
            gameStateController.togglePause();
            keyEvent.consume();
            return;
        }
        
        // Handle movement and rotation - only when game is active (not paused or game over)
        if (gameStateController.isPauseProperty().getValue() == Boolean.FALSE 
                && gameStateController.isGameOverProperty().getValue() == Boolean.FALSE) {
            
            // Handle LEFT movement (or RIGHT if controls are flipped)
            if (keyEvent.getCode() == KeyCode.LEFT || keyEvent.getCode() == KeyCode.A) {
                if (gameStateController.isControlsFlipped()) {
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
                if (gameStateController.isControlsFlipped()) {
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
            gameStateController.newGame(null);
        }
    }
}
