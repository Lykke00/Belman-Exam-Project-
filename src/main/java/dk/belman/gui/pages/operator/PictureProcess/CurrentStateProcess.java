package dk.belman.gui.pages.operator.PictureProcess;

import javafx.beans.property.SimpleStringProperty;

public enum CurrentStateProcess {
    NONE("None"),
    RIGHT("Right"),
    LEFT("Left"),
    FRONT("Front"),
    BACK("Back");

    private final String textProperty;

    CurrentStateProcess(String text) {
        this.textProperty = text;
    }

    public String textProperty() {
        return textProperty;
    }

    public static CurrentStateProcess nextState(CurrentStateProcess current) {
        int nextIndex = current.ordinal() + 1;
        CurrentStateProcess[] values = CurrentStateProcess.values();
        return nextIndex < values.length ? values[nextIndex] : NONE;
    }

    public static CurrentStateProcess previousState(CurrentStateProcess current) {
        int prevIndex = current.ordinal() - 1;
        return prevIndex >= 0 ? CurrentStateProcess.values()[prevIndex] : NONE;
    }
}
