package com.comp2042;

import com.comp2042.logic.game.DownData;
import com.comp2042.logic.game.ViewData;

public interface InputActionListener {

    DownData onDownEvent(MoveAction event);

    ViewData onLeftEvent(MoveAction event);

    ViewData onRightEvent(MoveAction event);

    ViewData onRotateEvent(MoveAction event);

    void createNewGame();
}
