package dk.belman.gui.pages;

import com.gluonhq.attach.barcodescan.BarcodeScanService;
import com.gluonhq.charm.glisten.application.AppManager;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.control.TextField;
import com.gluonhq.charm.glisten.mvc.View;
import dk.belman.gui.common.AuthModel;
import dk.belman.gui.interactors.AuthInteractor;
import dk.belman.gui.interactors.InteractorManager;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextFormatter;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController extends View implements Initializable {
    private final AuthInteractor authInteractor = InteractorManager.getInstance().getAuthInteractor();
    private final AuthModel model = authInteractor.getAuthModel();

    @FXML
    private TextField txtFieldUsername, txtFieldPassword;

    @FXML
    private Button btnLogin, btnScanQr;

    private BarcodeScanService scanService;


    private final StringProperty password = new SimpleStringProperty();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        validate();

        initializeScanService();
        onLogin();
        onScanQrCode();
        setupEnterKeyHandler();

        setupPasswordTxtField();
        onLogOut();
    }

    private void setupPasswordTxtField() {
        txtFieldPassword.setTextFormatter(new TextFormatter<String>(change -> {
            if (change.isContentChange()) {
                String newText = change.getControlNewText();

                password.set(newText);

                String maskedText = "*".repeat(newText.length());

                change.setText(maskedText);
                change.setRange(0, change.getControlText().length());
                change.setCaretPosition(newText.length());
                change.setAnchor(newText.length());
            }
            return change;
        }));
    }

    private void onLogOut() {
        model.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                txtFieldUsername.setText("");
                txtFieldPassword.setText("");
                model.logOut(false);
            }
        });
    }

    @Override
    public void updateAppBar(AppBar appbar) {
        AppManager appManager = AppManager.getInstance();
        appManager.getAppBar().setTitle(new Label("Belsign"));
    }

    private void initializeScanService() {
        BarcodeScanService.create().ifPresent(service -> {
            this.scanService = service;
            scanService.resultProperty().addListener((obs, ov, nv) ->
                    txtFieldUsername.setText(nv));
        });
    }

    private void onScanQrCode() {
        btnScanQr.setOnAction(e -> {
            if (scanService != null) {
                scanService.asyncScan();
            }
        });
    }

    private void validate() {
        BooleanBinding notValidFields = txtFieldUsername.textProperty().isEmpty()
                .or(txtFieldPassword.textProperty().isEmpty())
                .or(model.databaseLoadingProperty());

        btnLogin.textProperty().bind(
                Bindings.when(model.databaseLoadingProperty())
                        .then("Loading...")
                        .otherwise("Login")
        );
        btnLogin.disableProperty().bind(notValidFields);
    }

    private void onLogin() {
        btnLogin.setOnAction(event -> {
            String workerId = txtFieldUsername.getText();
            String pass = password.get();

            authInteractor.logIn(workerId, pass);
        });
    }

    private void setupEnterKeyHandler() {
        txtFieldUsername.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("ENTER")) {
                if (!txtFieldPassword.getText().isEmpty()) {
                    btnLogin.fire();
                } else {
                    txtFieldPassword.requestFocus();
                }
            }
        });

        txtFieldPassword.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("ENTER")) {
                if (!btnLogin.isDisabled()) {
                    btnLogin.fire();
                }
            }
        });
    }
}
