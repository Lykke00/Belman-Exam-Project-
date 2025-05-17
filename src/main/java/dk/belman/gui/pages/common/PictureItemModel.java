package dk.belman.gui.pages.common;

import dk.belman.be.ReportImage;
import dk.belman.gui.pages.operator.PictureProcess.CurrentStateProcess;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.image.Image;

import java.io.ByteArrayInputStream;

public class PictureItemModel {
    private final SimpleIntegerProperty id = new SimpleIntegerProperty();
    private final SimpleObjectProperty<Image> picture = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<CurrentStateProcess> state = new SimpleObjectProperty<>(CurrentStateProcess.BEGIN);
    private final SimpleStringProperty comment = new SimpleStringProperty();

    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public SimpleObjectProperty<Image> pictureProperty() {
        return picture;
    }

    public SimpleObjectProperty<CurrentStateProcess> stateProperty() {
        return state;
    }

    public SimpleStringProperty commentProperty() {
        return comment;
    }

    public static PictureItemModel fromEntity(ReportImage image) {
        PictureItemModel model = new PictureItemModel();
        model.id.set(image.getId());
        model.state.set(CurrentStateProcess.fromString(image.getTakenFromAngle()));
        model.picture.set(new Image(new ByteArrayInputStream(image.getImage())));
        model.comment.set(image.getComment());
        return model;
    }
}
