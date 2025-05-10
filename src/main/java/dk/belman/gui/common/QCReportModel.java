package dk.belman.gui.common;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;


public class QCReportModel {
    private final SimpleStringProperty qcReportId = new SimpleStringProperty();
    private final ObservableList<Image> images = FXCollections.observableArrayList();
    private final SimpleStringProperty comment = new SimpleStringProperty();

    private final SimpleBooleanProperty updatePreview = new SimpleBooleanProperty();

    public SimpleStringProperty qcReportIdProperty() {
        return qcReportId;
    }

    public ObservableList<Image> getImages() {
        return images;
    }

    private final List<ChangeListener<Boolean>> listeners = new ArrayList<>();

    public void addListener(ChangeListener<Boolean> listener) {
        listeners.add(listener);
        updatePreview.addListener(listener);
    }

    public void notifyListeners() {
        for (ChangeListener<Boolean> listener : listeners) {
            listener.changed(updatePreview, false, updatePreview.get());
        }
    }

    public void setPreviewProperty(boolean value) {
        updatePreview.set(value);
        notifyListeners();
    }


    public SimpleStringProperty commentProperty() {
        return comment;
    }

    public void reset() {
        qcReportId.set("");
        images.clear();
        updatePreview.set(false);
        comment.set("");
    }
}
