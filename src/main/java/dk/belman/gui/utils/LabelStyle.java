package dk.belman.gui.utils;

import javafx.scene.control.Label;

public class LabelStyle {
    public static Label getAppBarTitle(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: white; -fx-font-size: 20px;");
        return label;
    }
}
