package dk.belman.gui.pages.operator.PictureProcess;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

public class PictureProcessModel {
    private final SimpleStringProperty qcReportId = new SimpleStringProperty();

    //hjernen bag det hele og holder styr på ALT
    private final SimpleObjectProperty<CurrentStateProcess> state = new SimpleObjectProperty<>(CurrentStateProcess.BEGIN);

    // vi gemmer alle states i en liste, for at kunne samenligne med vores ovenstående state
    private final ObservableMap<CurrentStateProcess, PictureItemModel> stateList = FXCollections.observableHashMap();

    public PictureProcessModel() {
        initializeStateList();
    }

    private void initializeStateList() {
        for (CurrentStateProcess state : CurrentStateProcess.values()) {
            if (state.equals(CurrentStateProcess.BEGIN)
                    || state.equals(CurrentStateProcess.FINISH)) // vi har ikke brug for at gemme data på disse sider
                continue;

            stateList.put(state, new PictureItemModel());
        }
    }

    public SimpleStringProperty qcReportIdProperty() {
        return qcReportId;
    }

    public SimpleObjectProperty<CurrentStateProcess> stateProperty() {
        return state;
    }

    public ObservableMap<CurrentStateProcess, PictureItemModel> getStateList() {
        return stateList;
    }
}
