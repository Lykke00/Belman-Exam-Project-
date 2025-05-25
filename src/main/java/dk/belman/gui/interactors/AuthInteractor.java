package dk.belman.gui.interactors;

import com.gluonhq.charm.glisten.application.AppManager;
import dk.belman.bll.UserManager;
import dk.belman.gui.AppView;
import dk.belman.gui.common.AuthModel;
import dk.belman.gui.components.GluonSnackbar;
import dk.belman.gui.common.UserModel;
import dk.belman.gui.utils.BackgroundTaskExecutor;
import dk.belman.gui.utils.DialogHandler;

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

    public void logIn(String email, String password) {
        BackgroundTaskExecutor.executeWithExceptionHandling(
                () -> userManager.authenticateUser(email, password),
                user -> {
                    UserModel loggedInModel = UserModel.fromEntity(user);
                    authModel.userProperty().set(loggedInModel);

                    // bruges til at finde ud af hvilke side man skal logges ind på
                    decideUserView(loggedInModel);
                },
                exception -> {
                    GluonSnackbar.showSnackbar("Login failed... please try again");
                },
                loading -> {
                    authModel.databaseLoadingProperty().set(loading);
                }
        );
    }

    private void decideUserView(UserModel userModel) {
        if (userModel == null) {
            GluonSnackbar.showSnackbar("You are not logged in");
            return;
        }

        AppView appView = null;

        switch (userModel.roleProperty().get()) {
            case ADMIN:
                appView = AppView.ADMIN_USERS_VIEW;
                break;
            case OPERATOR:
                appView = AppView.OPERATOR_LANDING;
                break;
            case INSPECTOR:
                appView = AppView.INSPECTOR_VIEW_REPORTS;
                break;
            default:
                GluonSnackbar.showSnackbar("Unknown user role, please contact support");
        }

        if (appView != null) {
            GluonSnackbar.showSnackbar("Login succeeded");
            AppManager.getInstance().switchView(appView.getRoute());
        }
    }

    public AuthModel getAuthModel() {
        return authModel;
    }
}
