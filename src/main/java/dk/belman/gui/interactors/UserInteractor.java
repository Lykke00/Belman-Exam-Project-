package dk.belman.gui.interactors;

import dk.belman.be.User;
import dk.belman.bll.UserManager;
import dk.belman.gui.modals.usercreatenew.UserCreateNewModel;
import dk.belman.gui.pages.admin.users.AdminUsersModel;
import dk.belman.gui.common.UserModel;
import dk.belman.gui.utils.BackgroundTaskExecutor;
import dk.belman.gui.utils.DialogHandler;

import java.util.function.Consumer;

public class UserInteractor {
    private AdminUsersModel model;
    private UserCreateNewModel userCreateNewModel;

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

    public AdminUsersModel getAdminUsersModel() {
        return model;
    }

    public UserCreateNewModel getUserCreateNewModel() {
        return userCreateNewModel;
    }
}
