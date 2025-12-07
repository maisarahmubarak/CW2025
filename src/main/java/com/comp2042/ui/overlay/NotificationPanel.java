package com.comp2042.ui.overlay;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Glow;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class NotificationPanel extends BorderPane {

    private static final double PANEL_MIN_HEIGHT = 200;
    private static final double PANEL_MIN_WIDTH = 220;
    private static final double GLOW_LEVEL = 0.6;
    private static final Color BONUS_TEXT_COLOR = Color.WHITE;
    private static final Duration FADE_DURATION = Duration.millis(2000);
    private static final Duration FLOAT_DURATION = Duration.millis(2500);
    private static final double FLOAT_OFFSET = 40;

    public NotificationPanel(String text) {
        setMinHeight(PANEL_MIN_HEIGHT);
        setMinWidth(PANEL_MIN_WIDTH);
        final Label score = new Label(text);
        score.getStyleClass().add("bonusStyle");
        final Effect glow = new Glow(GLOW_LEVEL);
        score.setEffect(glow);
        score.setTextFill(BONUS_TEXT_COLOR);
        setCenter(score);

    }

    public void showScore(ObservableList<Node> list) {
        FadeTransition ft = new FadeTransition(FADE_DURATION, this);
        TranslateTransition tt = new TranslateTransition(FLOAT_DURATION, this);
        tt.setToY(this.getLayoutY() - FLOAT_OFFSET);
        ft.setFromValue(1);
        ft.setToValue(0);
        ParallelTransition transition = new ParallelTransition(tt, ft);
        transition.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                list.remove(NotificationPanel.this);
            }
        });
        transition.play();
    }

    public void animateCountdown(ObservableList<Node> list) {
        this.setScaleX(0.8);
        this.setScaleY(0.8);
        this.setOpacity(1.0);

        ScaleTransition st = new ScaleTransition(Duration.millis(150), this);
        st.setFromX(0.8);
        st.setFromY(0.8);
        st.setToX(1.0);
        st.setToY(1.0);

        PauseTransition pause = new PauseTransition(Duration.millis(850));
        pause.setOnFinished(e -> list.remove(this));

        SequentialTransition seq = new SequentialTransition(st, pause);
        seq.play();
    }
}
