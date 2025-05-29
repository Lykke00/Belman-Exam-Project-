package dk.belman.gui.modals.sendemail;

import dk.belman.gui.components.GluonSnackbar;
import dk.belman.gui.interactors.EmailInteractor;
import dk.belman.gui.interactors.InteractorManager;
import dk.belman.gui.modals.IModalController;
import dk.belman.gui.utils.ModalHandler;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class SendEmailController implements Initializable, IModalController {
    private final EmailInteractor emailInteractor = InteractorManager.getInstance().getEmailInteractor();

    @FXML
    private TextField txtFieldEmail;

    @FXML
    private TextArea txtAreaExtraMessage;

    @FXML
    private Button btnCancel, btnSend;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupButtons();
        validate();
    }

    @Override
    public void load() {
        this.txtFieldEmail.clear();
        this.txtAreaExtraMessage.clear();

        emailInteractor.fetchImagesForReport();
    }

    private void validate() {
        BooleanBinding emptyFields = txtFieldEmail.textProperty().isEmpty()
                .or(Bindings.createBooleanBinding(() -> !isValidEmail(txtFieldEmail.getText()), txtFieldEmail.textProperty()))
                .or(emailInteractor.getSendEmailModel().databaseLoadingProperty());

        btnSend.disableProperty().bind(emptyFields);
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email != null && email.matches(emailRegex);
    }

    private void setupButtons() {
        btnCancel.setOnAction(e -> {
            ModalHandler.getInstance().hideModal();
        });

        btnSend.setOnAction(this::sendEmail);
    }

    private void sendEmail(ActionEvent actionEvent) {
        String email = txtFieldEmail.getText();
        String extraMessage = txtAreaExtraMessage.getText();

        emailInteractor.sendQCReportEmail(email, extraMessage, sent -> {
            if (sent) {
                GluonSnackbar.showSnackbar("Email sent successfully!");
                ModalHandler.getInstance().hideModal();
            }
        });
    }
}
