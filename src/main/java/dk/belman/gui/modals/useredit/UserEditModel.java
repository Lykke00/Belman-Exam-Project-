package dk.belman.gui.modals.useredit;

import dk.belman.gui.common.UserModel;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

public class UserEditModel {
    private final SimpleObjectProperty<UserModel> user = new SimpleObjectProperty<>();

    private final SimpleBooleanProperty databaseLoading = new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty errorUserExists = new SimpleBooleanProperty(false);

    public SimpleObjectProperty<UserModel> userProperty() {
        return user;
    }

    public SimpleBooleanProperty databaseLoadingProperty() {
        return databaseLoading;
    }

    public SimpleBooleanProperty errorUserExistsProperty() {
        return errorUserExists;
    }
}
