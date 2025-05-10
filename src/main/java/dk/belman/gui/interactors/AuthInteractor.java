package dk.belman.gui.interactors;

import dk.belman.bll.UserManager;
import dk.belman.gui.common.AuthModel;
import dk.belman.gui.common.QCReportModel;
import dk.belman.gui.components.GluonSnackbar;
import dk.belman.gui.pages.common.UserModel;
import dk.belman.gui.utils.BackgroundTaskExecutor;
import dk.belman.gui.utils.DialogHandler;

import java.awt.*;
import java.util.function.Consumer;

public class AuthInteractor {
    private final AuthModel authModel;
    private UserManager userManager;

    public AuthInteractor() {
        this.authModel = new AuthModel();
        try {
            this.userManager = new UserManager();
        } catch (Exception e) {
            DialogHandler.showExceptionError("Error initializing UserManager", "An unexpected error occurred", e);
        }
    }

    public void logIn(String email, String password, Consumer<Boolean> callback) {
        BackgroundTaskExecutor.executeWithExceptionHandling(
                () -> userManager.authenticateUser(email, password),
                user -> {
                    UserModel loggedInModel = UserModel.fromEntity(user);
                    authModel.userProperty().set(loggedInModel);

                    callback.accept(true);
                },
                exception -> {
                    GluonSnackbar.showSnackbar("Login failed... please try again");
                    callback.accept(false);
                },
                loading -> {
                    authModel.databaseLoadingProperty().set(loading);
                }
        );
    }


    public AuthModel getAuthModel() {
        return authModel;
    }
}
