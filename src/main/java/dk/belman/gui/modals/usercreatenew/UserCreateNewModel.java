package dk.belman.gui.modals.usercreatenew;

import javafx.beans.property.SimpleBooleanProperty;

public class UserCreateNewModel {
    private final SimpleBooleanProperty databaseCreating = new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty errorUserExists = new SimpleBooleanProperty(false);

    public SimpleBooleanProperty databaseCreatingProperty() {
        return databaseCreating;
    }

    public SimpleBooleanProperty errorUserExistsProperty() {
        return errorUserExists;
    }
}
