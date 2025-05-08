package dk.belman.gui.common;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;

import java.util.ArrayList;
import java.util.List;

public class AuthModel {
    private QCReportModel qcReportModel;

    public AuthModel(QCReportModel model) {
        this.qcReportModel = model;
    }

    private final SimpleBooleanProperty logOut = new SimpleBooleanProperty();

    private final List<ChangeListener<Boolean>> listeners = new ArrayList<>();

    public void addListener(ChangeListener<Boolean> listener) {
        listeners.add(listener);
        logOut.addListener(listener);
    }

    public void notifyListeners() {
        for (ChangeListener<Boolean> listener : listeners) {
            listener.changed(logOut, false, logOut.get());
        }
    }

    public void logOut(boolean value) {
        if (value)
            qcReportModel.reset();

        logOut.set(value);
        notifyListeners();

    }
}
