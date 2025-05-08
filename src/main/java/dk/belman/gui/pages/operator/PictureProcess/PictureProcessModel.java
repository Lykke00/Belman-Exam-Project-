package dk.belman.gui.pages.operator.PictureProcess;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

public class PictureProcessModel {
    private final SimpleObjectProperty<CurrentStateProcess> state = new SimpleObjectProperty<>(CurrentStateProcess.NONE);
    private ObservableMap<CurrentStateProcess, ObservableList<PictureItemModel>> stateList = FXCollections.observableHashMap();

    public PictureProcessModel() {
        state.set(CurrentStateProcess.NONE);

        initializeStateList();
    }

    private void initializeStateList() {
        for (CurrentStateProcess state : CurrentStateProcess.values()) {
            if (state.equals(CurrentStateProcess.NONE))
                continue;

            stateList.put(state, FXCollections.observableArrayList());
        }
    }

    public SimpleObjectProperty<CurrentStateProcess> stateProperty() {
        return state;
    }

    public ObservableMap<CurrentStateProcess, ObservableList<PictureItemModel>> getStateList() {
        return stateList;
    }
}
