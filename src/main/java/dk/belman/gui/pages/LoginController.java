package dk.belman.gui.pages;

import com.gluonhq.attach.barcodescan.BarcodeScanService;
import com.gluonhq.charm.glisten.application.AppManager;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.control.Snackbar;
import com.gluonhq.charm.glisten.control.TextField;
import com.gluonhq.charm.glisten.mvc.View;
import dk.belman.gui.AppView;
import dk.belman.gui.Routes;
import dk.belman.gui.common.AuthModel;
import dk.belman.gui.components.GluonSnackbar;
import dk.belman.gui.interactors.AuthInteractor;
import dk.belman.gui.interactors.InteractorManager;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

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


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        validate();

        initializeScanService();
        onLogin();
        onScanQrCode();
        setupEnterKeyHandler();

        onLogOut();
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

        btnLogin.disableProperty().bind(notValidFields);
    }

    private void onLogin() {
        btnLogin.setOnAction(event -> {
            String workerId = txtFieldUsername.getText();
            String password = txtFieldPassword.getText();

            authInteractor.logIn(workerId, password, success -> {
                if (!success) {
                    GluonSnackbar.showSnackbar("Login failed", "OK", () -> {
                        GluonSnackbar.hideSnackbar();
                    });
                    return;
                }

                GluonSnackbar.showSnackbar("Login succeeded");
                AppManager.getInstance().switchView(AppView.OPERATOR_LANDING.getRoute());
            });
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
