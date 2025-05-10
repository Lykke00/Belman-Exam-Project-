package dk.belman.gui.pages.common;

import dk.belman.be.User;
import dk.belman.enums.UserRole;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class UserModel {
    private final SimpleIntegerProperty id = new SimpleIntegerProperty();
    private final SimpleStringProperty firstName = new SimpleStringProperty();
    private final SimpleStringProperty lastName = new SimpleStringProperty();
    private final SimpleObjectProperty<UserRole> role = new SimpleObjectProperty<>();

    public UserModel(int id, String firstName, String lastName, UserRole role) {
        this.id.set(id);
        this.firstName.set(firstName);
        this.lastName.set(lastName);
        this.role.set(role);
    }

    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public SimpleStringProperty firstNameProperty() {
        return firstName;
    }

    public SimpleStringProperty lastNameProperty() {
        return lastName;
    }

    public SimpleObjectProperty<UserRole> roleProperty() {
        return role;
    }

    public static UserModel fromEntity(User user) {
        return new UserModel(user.getId(), user.getFirstName(), user.getLastName(), user.getRole());
    }

    public static User toEntity(UserModel userModel) {
        return new User(userModel.id.get(), userModel.firstName.get(), userModel.lastName.get(), userModel.role.get());
    }
}