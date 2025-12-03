package com.comp2042;

public interface InputActionListener {

    DownData onDownEvent(MoveAction event);

    ViewData onLeftEvent(MoveAction event);

    ViewData onRightEvent(MoveAction event);

    ViewData onRotateEvent(MoveAction event);

    void createNewGame();
}
