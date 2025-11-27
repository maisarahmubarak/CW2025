package com.comp2042;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class GuiController implements Initializable {

    private static final int BRICK_SIZE = 20;
    private static final String SCORE_FONT_RESOURCE = "digital.ttf";
    private static final double SCORE_FONT_SIZE = 38;
    private static final Duration DEFAULT_DROP_INTERVAL = Duration.millis(400);

    @FXML
    private GridPane gamePanel;

    @FXML
    private Group groupNotification;

    @FXML
    private GridPane brickPanel;

    @FXML
    private GameOverPanel gameOverPanel;

    private InputActionListener eventListener;

    private BoardView boardView;

    private GameLoop gameLoop;

    private BrickColorPalette palette = new ClassicBrickPalette();

    private InputAdapter<KeyEvent> inputAdapter = new KeyEventInputAdapter();

    private InputSource inputSource;

    private RenderLoop renderLoop;

    private volatile ViewData latestViewData;
    private volatile int[][] latestBoardSnapshot;
    private volatile boolean backgroundDirty;

    private GameState currentState;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Font.loadFont(getClass().getClassLoader().getResource(SCORE_FONT_RESOURCE).toExternalForm(), SCORE_FONT_SIZE);
        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();
        boardView = new JavaFxBoardView(gamePanel, brickPanel, BRICK_SIZE);
        boardView.setPalette(palette);
        gameLoop = new GameLoop(DEFAULT_DROP_INTERVAL, () -> moveDown(new MoveAction(ActionType.DOWN, ActionSource.THREAD)));
        inputSource = new JavaFxInputSource(gamePanel, inputAdapter);
        inputSource.bind(this::handleInputEvent);
        renderLoop = new RenderLoop(this::renderFrame);
        renderLoop.play();
        gameOverPanel.setVisible(false);
        currentState = new RunningState();
        currentState.onEnter(this);
    }

    public void initGameView(int[][] boardMatrix, ViewData brick) {
        boardView.initGameView(boardMatrix, brick);
        latestViewData = brick;
        latestBoardSnapshot = MatrixOperations.copy(boardMatrix);
        backgroundDirty = true;
        setState(new RunningState());
    }

    public void refreshGameBackground(int[][] board) {
        latestBoardSnapshot = MatrixOperations.copy(board);
        backgroundDirty = true;
    }

    public void setInputAdapter(InputAdapter<KeyEvent> inputAdapter) {
        if (inputAdapter != null) {
            this.inputAdapter = inputAdapter;
            if (inputSource != null) {
                inputSource.setAdapter(inputAdapter);
            }
        }
    }

    public void setColorPalette(BrickColorPalette palette) {
        if (palette == null) {
            return;
        }
        this.palette = palette;
        if (boardView != null) {
            boardView.setPalette(palette);
        }
    }

    private void moveDown(MoveAction event) {
        if (currentState != null && currentState.canDrop() && eventListener != null) {
            DownData downData = eventListener.onDownEvent(event);
            if (downData != null) {
                if (downData.getClearRow() != null && downData.getClearRow().getLinesRemoved() > 0) {
                    NotificationPanel notificationPanel = new NotificationPanel("+" + downData.getClearRow().getScoreBonus());
                    groupNotification.getChildren().add(notificationPanel);
                    notificationPanel.showScore(groupNotification.getChildren());
                }
                queueViewData(downData.getViewData());
            }
        }
        gamePanel.requestFocus();
    }

    public void setEventListener(InputActionListener eventListener) {
        this.eventListener = eventListener;
    }

    public void gameOver() {
        gameOverPanel.setVisible(true);
        setState(new GameOverState());
    }

    public void newGame(ActionEvent actionEvent) {
        if (eventListener != null) {
            eventListener.createNewGame();
        }
        gamePanel.requestFocus();
        setState(new RunningState());
    }

    public void pauseGame(ActionEvent actionEvent) {
        if (currentState instanceof RunningState) {
            setState(new PausedState());
        } else if (currentState instanceof PausedState) {
            setState(new RunningState());
        }
        gamePanel.requestFocus();
    }

    private void setState(GameState nextState) {
        if (nextState == null || nextState == currentState) {
            return;
        }
        if (currentState != null) {
            currentState.onExit(this);
        }
        currentState = nextState;
        currentState.onEnter(this);
    }

    private void handleInputEvent(InputEvent inputEvent) {
        if (inputEvent == null || currentState == null) {
            return;
        }
        currentState.handleInputEvent(this, inputEvent);
    }

    private void renderFrame() {
        if (backgroundDirty && latestBoardSnapshot != null) {
            boardView.refreshGameBackground(latestBoardSnapshot);
            backgroundDirty = false;
        }
        if (latestViewData != null) {
            boardView.refreshBrick(latestViewData);
            latestViewData = null;
        }
    }

    private void queueViewData(ViewData viewData) {
        if (viewData != null) {
            latestViewData = viewData;
        }
    }

    private interface GameState {
        void handleInputEvent(GuiController controller, InputEvent inputEvent);

        void onEnter(GuiController controller);

        void onExit(GuiController controller);

        boolean canDrop();
    }

    private class RunningState implements GameState {
        @Override
        public void handleInputEvent(GuiController controller, InputEvent inputEvent) {
            if (inputEvent.getKind() == InputEvent.Kind.NEW_GAME) {
                controller.newGame(null);
                return;
            }
            if (controller.eventListener == null || inputEvent.getMoveAction() == null) {
                return;
            }
            MoveAction moveAction = inputEvent.getMoveAction();
            switch (moveAction.getActionType()) {
                case LEFT:
                    controller.queueViewData(controller.eventListener.onLeftEvent(moveAction));
                    break;
                case RIGHT:
                    controller.queueViewData(controller.eventListener.onRightEvent(moveAction));
                    break;
                case ROTATE:
                    controller.queueViewData(controller.eventListener.onRotateEvent(moveAction));
                    break;
                case DOWN:
                    controller.moveDown(moveAction);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onEnter(GuiController controller) {
            controller.gamePanel.requestFocus();
            controller.gameOverPanel.setVisible(false);
            controller.gameLoop.play();
        }

        @Override
        public void onExit(GuiController controller) {
            controller.gameLoop.stop();
        }

        @Override
        public boolean canDrop() {
            return true;
        }
    }

    private class PausedState implements GameState {
        @Override
        public void handleInputEvent(GuiController controller, InputEvent inputEvent) {
            if (inputEvent.getKind() == InputEvent.Kind.NEW_GAME) {
                controller.newGame(null);
            }
        }

        @Override
        public void onEnter(GuiController controller) {
            controller.gameLoop.stop();
        }

        @Override
        public void onExit(GuiController controller) {
            // returning to running state will restart the loop
        }

        @Override
        public boolean canDrop() {
            return false;
        }
    }

    private class GameOverState implements GameState {
        @Override
        public void handleInputEvent(GuiController controller, InputEvent inputEvent) {
            if (inputEvent.getKind() == InputEvent.Kind.NEW_GAME) {
                controller.newGame(null);
            }
        }

        @Override
        public void onEnter(GuiController controller) {
            controller.gameLoop.stop();
        }

        @Override
        public void onExit(GuiController controller) {
            // no-op
        }

        @Override
        public boolean canDrop() {
            return false;
        }
    }
}
