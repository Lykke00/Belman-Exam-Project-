package dk.belman.gui.pages.operator.PictureProcess;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

public class PictureProcessModel {

    //hjernen bag det hele og holder styr på ALT
    private final SimpleObjectProperty<CurrentStateProcess> state = new SimpleObjectProperty<>(CurrentStateProcess.NONE);

    // vi gemmer alle states i en liste, for at kunne samenligne med vores ovenstående state
    private final ObservableMap<CurrentStateProcess, PictureItemModel> stateList = FXCollections.observableHashMap();

    public PictureProcessModel() {
        initializeStateList();
    }

    private void initializeStateList() {
        for (CurrentStateProcess state : CurrentStateProcess.values()) {
            if (state.equals(CurrentStateProcess.NONE))
                continue;

            stateList.put(state, new PictureItemModel());
        }
    }

    public SimpleObjectProperty<CurrentStateProcess> stateProperty() {
        return state;
    }

    public ObservableMap<CurrentStateProcess, PictureItemModel> getStateList() {
        return stateList;
    }
}
