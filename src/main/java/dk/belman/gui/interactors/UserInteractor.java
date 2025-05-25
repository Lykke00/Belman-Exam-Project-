package dk.belman.gui.interactors;

import dk.belman.be.User;
import dk.belman.bll.UserManager;
import dk.belman.gui.modals.usercreatenew.UserCreateNewModel;
import dk.belman.gui.modals.useredit.UserEditModel;
import dk.belman.gui.pages.admin.users.AdminUsersModel;
import dk.belman.gui.common.UserModel;
import dk.belman.gui.utils.BackgroundTaskExecutor;
import dk.belman.gui.utils.DialogHandler;

import java.util.function.Consumer;

public class UserInteractor {
    private AdminUsersModel model;
    private UserCreateNewModel userCreateNewModel;
    private UserEditModel userEditModel;

    private UserManager userManager;

    public UserInteractor() {
        try {
            this.userManager = new UserManager();
        } catch (Exception e) {
            DialogHandler.showExceptionError("Error initializing ReportManager", "Couldn't initialize ReportManager, an unexpected error occurred", e);
        }

        initialize();
    }

    public void initialize() {
        this.model = new AdminUsersModel();
        this.userCreateNewModel = new UserCreateNewModel();
        this.userEditModel = new UserEditModel();

        getAllUsers();
    }

    public void getAllUsers() {
        BackgroundTaskExecutor.executeWithExceptionHandling(
                () -> userManager.getAll(),
                users -> {
                    model.observableUsersList().setAll(users.stream()
                            .map(UserModel::fromEntity)
                            .toList());
                },
                error -> {
                    DialogHandler.showExceptionError("Error loading users", "Couldn't load users from database, an unexpected error occurred", error);
                },
                loading -> {
                    model.databaseLoadingProperty().set(loading);
                }
        );
    }

    public void createUser(User user, Consumer<Boolean> callback) {
        BackgroundTaskExecutor.executeWithExceptionHandling(
                () -> userManager.registerUser(user),
                created -> {
                    UserModel createdToModel = UserModel.fromEntity(created);
                    model.observableUsersList().add(createdToModel);
                    callback.accept(true);
                },
                error -> {
                    boolean userExists = error.getMessage().equals("USER_EXISTS");
                    userCreateNewModel.errorUserExistsProperty().set(userExists);

                    callback.accept(false);

                    if (userExists)
                        return;

                    DialogHandler.showExceptionError("Error creating user", "Couldn't create user, an unexpected error occurred", error);
                },
                loading -> {
                    userCreateNewModel.databaseCreatingProperty().set(loading);
                }
        );
    }

    public void editUser(UserModel newData, UserModel oldUser, Consumer<Boolean> callback) {
        BackgroundTaskExecutor.executeWithExceptionHandling(
                () -> {
                    User edited = UserModel.toEntity(newData);
                    User oldUserEntity = UserModel.toEntity(oldUser);

                    return userManager.editUser(edited, oldUserEntity);
                },
                updated -> {
                    oldUser.update(newData);

                    callback.accept(true);
                },
                error -> {
                    boolean userExists = error.getMessage().equals("USER_EXISTS");
                    userEditModel.errorUserExistsProperty().set(userExists);

                    callback.accept(false);

                    if (userExists)
                        return;

                    DialogHandler.showExceptionError("Error editing user", "Couldn't edit user, an unexpected error occurred", error);
                },
                loading -> {
                    userEditModel.databaseLoadingProperty().set(loading);
                }
        );
    }

    public void updateUserStatus(UserModel user, boolean active, Consumer<Boolean> callback) {
        BackgroundTaskExecutor.executeWithExceptionHandling(
                () -> userManager.updateUserStatus(UserModel.toEntity(user), active),
                updated -> {
                    user.activeProperty().set(active);
                    callback.accept(true);
                },
                error -> {
                    DialogHandler.showExceptionError("Error updating user status", "Couldn't update user status, an unexpected error occurred", error);
                    callback.accept(false);
                },
                loading -> {
                    userEditModel.databaseLoadingProperty().set(loading);
                }
        );
    }

    public AdminUsersModel getAdminUsersModel() {
        return model;
    }

    public UserCreateNewModel getUserCreateNewModel() {
        return userCreateNewModel;
    }

    public UserEditModel getUserEditModel() {
        return userEditModel;
    }
}
