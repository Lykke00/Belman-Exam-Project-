package dk.belman.gui.pages.operator.PictureProcess;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.image.Image;

public class PictureItemModel {
    private final SimpleObjectProperty<Image> picture = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<CurrentStateProcess> state = new SimpleObjectProperty<>(CurrentStateProcess.BEGIN);
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
