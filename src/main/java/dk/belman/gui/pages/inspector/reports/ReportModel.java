package dk.belman.gui.pages.inspector.reports;

import dk.belman.gui.pages.common.ReportItemModel;
import javafx.beans.Observable;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ReportModel {
    private final ObservableList<ReportItemModel> reports = FXCollections.observableArrayList();
    private final SimpleBooleanProperty loaded = new SimpleBooleanProperty();

    public ObservableList<ReportItemModel> reportsProperty() {
        return reports;
    }

    public SimpleBooleanProperty loadedProperty() {
        return loaded;
    }
}
