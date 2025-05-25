package dk.belman.gui.pages.admin.users;

import dk.belman.gui.common.UserModel;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class AdminUsersModel {
    private final ObservableList<UserModel> users = FXCollections.observableArrayList();

    private final SimpleBooleanProperty databaseLoading = new SimpleBooleanProperty(false);

    public ObservableList<UserModel> observableUsersList() {
        return users;
    }

    public SimpleBooleanProperty databaseLoadingProperty() {
        return databaseLoading;
    }
}
