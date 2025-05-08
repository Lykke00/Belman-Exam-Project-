package dk.belman.gui.pages.operator.PictureProcess;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.awt.*;

public class PictureItemModel {
    private final SimpleObjectProperty<Image> picture = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<CurrentStateProcess> state = new SimpleObjectProperty<>(CurrentStateProcess.NONE);
    private final SimpleStringProperty comment = new SimpleStringProperty();

    public SimpleObjectProperty<Image> pictureProperty() {
        return picture;
    }

    public SimpleObjectProperty<CurrentStateProcess> stateProperty() {
        return state;
    }

    public SimpleStringProperty commentProperty() {
        return comment;
    }
}
