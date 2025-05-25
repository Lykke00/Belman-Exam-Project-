package dk.belman.gui.common;

import dk.belman.be.User;
import dk.belman.enums.UserRole;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class UserModel {
    private final SimpleIntegerProperty id = new SimpleIntegerProperty();
    private final SimpleStringProperty workerId = new SimpleStringProperty();
    private final SimpleStringProperty firstName = new SimpleStringProperty();
    private final SimpleStringProperty lastName = new SimpleStringProperty();
    private final SimpleObjectProperty<UserRole> role = new SimpleObjectProperty<>();
    private final SimpleBooleanProperty active = new SimpleBooleanProperty();

    private final SimpleStringProperty password = new SimpleStringProperty();

    public UserModel(int id, String workerId, String firstName, String lastName, UserRole role, boolean active) {
        this.id.set(id);
        this.workerId.set(workerId);
        this.firstName.set(firstName);
        this.lastName.set(lastName);
        this.role.set(role);
        this.active.set(active);
    }

    public UserModel(int id, String workerId, String firstName, String lastName, String password, UserRole role, boolean active) {
        this.id.set(id);
        this.workerId.set(workerId);
        this.firstName.set(firstName);
        this.lastName.set(lastName);
        this.password.set(password);
        this.role.set(role);
        this.active.set(active);
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

    public SimpleStringProperty workerIdProperty() {
        return workerId;
    }

    public SimpleBooleanProperty activeProperty() {
        return active;
    }

    public SimpleStringProperty passwordProperty() {
        return password;
    }

    public static UserModel fromEntity(User user) {
        return new UserModel(user.getId(), user.getWorkerId(), user.getFirstName(), user.getLastName(), user.getRole(), user.isActive());
    }

    public static User toEntity(UserModel userModel) {
        return new User(userModel.id.get(), userModel.workerId.get(), userModel.firstName.get(), userModel.lastName.get(), userModel.role.get(), userModel.active.get());
    }

    public void update(UserModel userModel) {
        this.id.set(userModel.id.get());
        this.workerId.set(userModel.workerId.get());
        this.firstName.set(userModel.firstName.get());
        this.lastName.set(userModel.lastName.get());
        this.role.set(userModel.role.get());
        this.active.set(userModel.active.get());
    }
}