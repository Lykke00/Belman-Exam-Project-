package dk.belman.gui.common;

import dk.belman.gui.pages.common.UserModel;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;

import java.util.ArrayList;
import java.util.List;

public class AuthModel {
    public AuthModel() {}

    private final SimpleBooleanProperty databaseLoading = new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty loginFailed = new SimpleBooleanProperty(false);

    private final SimpleObjectProperty<UserModel> user = new SimpleObjectProperty<>(null);

    private final SimpleBooleanProperty logOut = new SimpleBooleanProperty();


    public SimpleBooleanProperty databaseLoadingProperty() {
        return databaseLoading;
    }

    public SimpleBooleanProperty loginFailedProperty() {
        return loginFailed;
    }

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
        logOut.set(value);
        notifyListeners();

    }

    public SimpleObjectProperty<UserModel> userProperty() {
        return user;
    }
}
