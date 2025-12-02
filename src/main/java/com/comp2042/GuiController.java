package com.comp2042;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class GuiController implements Initializable {

    private static final int BRICK_SIZE = 20;

    @FXML
    private GridPane gamePanel;

    @FXML
    private Group groupNotification;

    @FXML
    private GridPane brickPanel;

    @FXML
    private GridPane previewPanel;

    @FXML
    private GameOverPanel gameOverPanel;

    private InputActionListener eventListener;

    private GameBoardView gameBoardView;

    private GameLoop gameLoop;

    private BrickColorPalette palette = new ClassicBrickPalette();

    private final BooleanProperty isPause = new SimpleBooleanProperty();

    private final BooleanProperty isGameOver = new SimpleBooleanProperty();

    private Timeline resumeCountdown;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Font.loadFont(getClass().getClassLoader().getResource("digital.ttf").toExternalForm(), 38);
        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();
        gamePanel.setOnKeyPressed(new GameKeyHandler(this));
        gameBoardView = new GameBoardView(gamePanel, brickPanel, previewPanel, BRICK_SIZE);
        gameBoardView.setPalette(palette);
        brickPanel.toFront();
        gameLoop = new GameLoop(Duration.millis(400), () -> moveDown(new MoveAction(ActionType.DOWN, ActionSource.THREAD)));
        gameOverPanel.setVisible(false);

    }

    // Moved the anonymous key handler here so key-handling logic lives in one place
    // and can be delegated to from a separate GameKeyHandler class.
    public void handleKeyEvent(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.P) {
            togglePause();
            keyEvent.consume();
            return;
        }
        if (isPause.getValue() == Boolean.FALSE && isGameOver.getValue() == Boolean.FALSE) {
            if (keyEvent.getCode() == KeyCode.LEFT || keyEvent.getCode() == KeyCode.A) {
                gameBoardView.refreshBrick(eventListener.onLeftEvent(new MoveAction(ActionType.LEFT, ActionSource.USER)));
                keyEvent.consume();
            }
            if (keyEvent.getCode() == KeyCode.RIGHT || keyEvent.getCode() == KeyCode.D) {
                gameBoardView.refreshBrick(eventListener.onRightEvent(new MoveAction(ActionType.RIGHT, ActionSource.USER)));
                keyEvent.consume();
            }
            if (keyEvent.getCode() == KeyCode.UP || keyEvent.getCode() == KeyCode.W) {
                gameBoardView.refreshBrick(eventListener.onRotateEvent(new MoveAction(ActionType.ROTATE, ActionSource.USER)));
                keyEvent.consume();
            }
            if (keyEvent.getCode() == KeyCode.DOWN || keyEvent.getCode() == KeyCode.S) {
                moveDown(new MoveAction(ActionType.DOWN, ActionSource.USER));
                keyEvent.consume();
            }
        }
        if (keyEvent.getCode() == KeyCode.N) {
            newGame(null);
        }
    }

    public void initGameView(int[][] boardMatrix, ViewData brick) {
        gameBoardView.initGameView(boardMatrix, brick);
        gameLoop.play();
    }

    public void refreshGameBackground(int[][] board) {
        gameBoardView.refreshGameBackground(board);
    }

    public void setColorPalette(BrickColorPalette palette) {
        if (palette == null) {
            return;
        }
        this.palette = palette;
        if (gameBoardView != null) {
            gameBoardView.setPalette(palette);
        }
    }

    private void moveDown(MoveAction event) {
        if (isPause.getValue() == Boolean.FALSE) {
            DownData downData = eventListener.onDownEvent(event);
            if (downData.getClearRow() != null && downData.getClearRow().getLinesRemoved() > 0) {
                NotificationPanel notificationPanel = new NotificationPanel("+" + downData.getClearRow().getScoreBonus());
                groupNotification.getChildren().add(notificationPanel);
                notificationPanel.showScore(groupNotification.getChildren());
            }
            gameBoardView.refreshBrick(downData.getViewData());
        }
        gamePanel.requestFocus();
    }

    public void setEventListener(InputActionListener eventListener) {
        this.eventListener = eventListener;
    }

    public void gameOver() {
        gameLoop.stop();
        cancelResumeCountdown();
        gameOverPanel.setVisible(true);
        isGameOver.setValue(Boolean.TRUE);
    }

    public void newGame(ActionEvent actionEvent) {
        gameLoop.stop();
        cancelResumeCountdown();
        gameOverPanel.setVisible(false);
        eventListener.createNewGame();
        gamePanel.requestFocus();
        gameLoop.play();
        isPause.setValue(Boolean.FALSE);
        isGameOver.setValue(Boolean.FALSE);
    }

    public void pauseGame(ActionEvent actionEvent) {
        togglePause();
    }

    private void togglePause() {
        if (isGameOver.getValue()) {
            return;
        }
        if (!isPause.getValue()) {
            isPause.setValue(Boolean.TRUE);
            gameLoop.stop();
            cancelResumeCountdown();
        } else {
            if (resumeCountdown != null) {
                cancelResumeCountdown();
            } else {
                beginResumeCountdown();
            }
        }
        gamePanel.requestFocus();
    }

    private void beginResumeCountdown() {
        final int[] remaining = {3};
        resumeCountdown = new Timeline(
                new KeyFrame(Duration.ZERO, e -> showCountdown(String.valueOf(remaining[0]--))),
                new KeyFrame(Duration.seconds(1))
        );
        resumeCountdown.setCycleCount(3);
        resumeCountdown.setOnFinished(e -> {
            isPause.setValue(Boolean.FALSE);
            gameLoop.play();
            resumeCountdown = null;
        });
        resumeCountdown.playFromStart();
    }

    private void showCountdown(String text) {
        NotificationPanel panel = new NotificationPanel(text);
        centerOverlay(panel);
        groupNotification.getChildren().add(panel);
        panel.showScore(groupNotification.getChildren());
    }

    private void centerOverlay(NotificationPanel panel) {
        double areaWidth = gamePanel.getBoundsInParent().getWidth();
        double areaHeight = gamePanel.getBoundsInParent().getHeight();
        double baseX = gamePanel.getLayoutX();
        double baseY = gamePanel.getLayoutY();
        double centeredX = baseX + Math.max(0, (areaWidth - panel.getMinWidth()) / 2);
        double centeredY = baseY + Math.max(0, (areaHeight - panel.getMinHeight()) / 2);
        panel.setLayoutX(centeredX);
        panel.setLayoutY(centeredY);
    }

    private void cancelResumeCountdown() {
        if (resumeCountdown != null) {
            resumeCountdown.stop();
            resumeCountdown = null;
        }
    }
}
