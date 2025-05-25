package dk.belman.gui.modals.usercreatenew;

import atlantafx.base.theme.Styles;
import dk.belman.be.User;
import dk.belman.enums.UserRole;
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

public class UserCreateNewModalController implements Initializable, IModalController {
    private final UserInteractor userInteractor = InteractorManager.getInstance().getUserInteractor();
    private final UserCreateNewModel model = userInteractor.getUserCreateNewModel();

    @FXML
    private ChoiceBox<UserRole> choiceBoxRole;

    @FXML
    private Button btnGenerateId, btnGeneratePassword, btnCancel, btnCreate;

    @FXML
    public TextField txtFieldWorkerId, txtFieldFirstName, txtFieldLastName, txtFieldPassword;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupControls();
        setupBinding();
        setupStyling();
        setupUserExists();
    }

    @Override
    public void load() {
        txtFieldPassword.setText("");
        txtFieldFirstName.setText("");
        txtFieldLastName.setText("");
        txtFieldWorkerId.setText("");
        choiceBoxRole.getSelectionModel().selectFirst();
        txtFieldWorkerId.requestFocus();
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

    private void setupStyling() {
        btnCreate.getStyleClass().add(Styles.ACCENT);
    }

    private void setupControls() {
        btnGenerateId.setOnAction(event -> generateId());
        btnGeneratePassword.setOnAction(event -> generatePassword());
        btnCancel.setOnAction(event -> cancel());
        btnCreate.setOnAction(event -> createUser());
        choiceBoxRole.getItems().addAll(UserRole.values());
        choiceBoxRole.getSelectionModel().selectFirst();
    }

    private void setupBinding() {
        btnCreate.disableProperty().bind(txtFieldWorkerId.textProperty().isEmpty()
                .or(txtFieldWorkerId.textProperty().length().lessThan(4))
                .or(txtFieldFirstName.textProperty().isEmpty())
                .or(txtFieldLastName.textProperty().isEmpty())
                .or(txtFieldPassword.textProperty().isEmpty())
                .or(choiceBoxRole.getSelectionModel().selectedItemProperty().isNull())
                .or(model.databaseCreatingProperty()));
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

    private void createUser() {
        String workerId = txtFieldWorkerId.getText();
        String firstName = txtFieldFirstName.getText();
        String lastName = txtFieldLastName.getText();
        String password = txtFieldPassword.getText();

        UserRole userRole = choiceBoxRole.getValue();

        User user = new User(workerId, firstName, lastName, password, userRole);
        userInteractor.createUser(user, created -> {
            if (created) {
                ModalHandler.getInstance().hideModal();
            }
        });
    }
}
