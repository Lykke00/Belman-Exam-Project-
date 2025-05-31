package dk.belman.gui.modals.useredit;

import atlantafx.base.theme.Styles;
import com.gluonhq.charm.glisten.mvc.View;
import dk.belman.enums.UserRole;
import dk.belman.gui.common.UserModel;
import dk.belman.gui.components.GluonSnackbar;
import dk.belman.gui.interactors.InteractorManager;
import dk.belman.gui.interactors.UserInteractor;
import dk.belman.gui.modals.IModalController;
import dk.belman.gui.utils.ModalHandler;
import dk.belman.gui.utils.PasswordGenerator;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class UserEditController extends View implements Initializable, IModalController {
    private final UserInteractor userInteractor = InteractorManager.getInstance().getUserInteractor();
    private final UserEditModel model = userInteractor.getUserEditModel();

    @FXML
    private ChoiceBox<UserRole> choiceBoxRole;

    @FXML
    private Button btnGenerateId, btnGeneratePassword, btnCancel, btnUpdate;

    @FXML
    public TextField txtFieldWorkerId, txtFieldFirstName, txtFieldLastName, txtFieldPassword;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.load();

        setupControls();
        setupUserExists();
    }

    @Override
    public void load() {
        UserModel user = model.userProperty().get();

        txtFieldWorkerId.setText(user.workerIdProperty().get());
        txtFieldPassword.setPromptText("Leave empty to keep current password");
        txtFieldFirstName.setText(user.firstNameProperty().get());
        txtFieldLastName.setText(user.lastNameProperty().get());
        choiceBoxRole.getItems().setAll(UserRole.values());
        choiceBoxRole.getSelectionModel().select(user.roleProperty().get());
        setupBinding();
    }

    private void setupUserExists() {
        model.errorUserExistsProperty().addListener((observable, oldValue, exists) -> {
            txtFieldWorkerId.pseudoClassStateChanged(Styles.STATE_DANGER, false);

            if (exists) {
                txtFieldWorkerId.pseudoClassStateChanged(Styles.STATE_DANGER, true);
            }
        });

        txtFieldWorkerId.textProperty().addListener((observable, oldValue, newValue) -> {
            txtFieldWorkerId.pseudoClassStateChanged(Styles.STATE_DANGER, false);
        });
    }

    private void setupBinding() {
        btnUpdate.disableProperty().bind(txtFieldWorkerId.textProperty().isEmpty()
                .or(txtFieldWorkerId.textProperty().length().lessThan(4))
                .or(txtFieldFirstName.textProperty().isEmpty())
                .or(txtFieldLastName.textProperty().isEmpty())
                .or(choiceBoxRole.getSelectionModel().selectedItemProperty().isNull())
                .or(model.databaseLoadingProperty()));
    }

    private void setupControls() {
        btnGenerateId.setOnAction(event -> generateId());
        btnGeneratePassword.setOnAction(event -> generatePassword());
        btnCancel.setOnAction(event -> cancel());
        btnUpdate.setOnAction(event -> editUser());
    }

    private void generateId() {
        String generated = PasswordGenerator.generatePassword(4, 1);
        txtFieldWorkerId.setText(generated.toUpperCase());
    }

    private void generatePassword() {
        String password = PasswordGenerator.generatePassword(6, 1);
        txtFieldPassword.setText(password);
    }

    private void cancel() {
        ModalHandler.getInstance().hideModal();
    }

    private void editUser() {
        UserModel user = model.userProperty().get();

        String workerId = txtFieldWorkerId.getText();
        String firstName = txtFieldFirstName.getText();
        String lastName = txtFieldLastName.getText();
        String password = txtFieldPassword.getText();

        UserRole userRole = choiceBoxRole.getValue();

        UserModel updatedUser = new UserModel(
                user.idProperty().get(),
                workerId,
                firstName,
                lastName,
                password,
                userRole,
                user.activeProperty().get()
        );

        userInteractor.editUser(updatedUser, user, updated -> {
            if (updated) {
                GluonSnackbar.showSnackbar("User updated successfully");
                ModalHandler.getInstance().hideModal();
            }
        });
    }

}
