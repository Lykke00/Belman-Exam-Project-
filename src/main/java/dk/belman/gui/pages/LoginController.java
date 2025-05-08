package dk.belman.gui.pages;

import com.gluonhq.attach.barcodescan.BarcodeScanService;
import com.gluonhq.charm.glisten.application.AppManager;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.control.TextField;
import com.gluonhq.charm.glisten.mvc.View;
import dk.belman.gui.Routes;
import dk.belman.gui.common.AuthModel;
import dk.belman.gui.interactors.InteractorManager;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController extends View implements Initializable {
    private final AuthModel model = InteractorManager.getInstance().getAuthInteractor().getAuthModel();

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
                .or(txtFieldPassword.textProperty().isEmpty());

        btnLogin.disableProperty().bind(notValidFields);
    }

    private void onLogin() {
        btnLogin.setOnAction(event -> {
            AppManager.getInstance().switchView(Routes.OPERATOR_LANDING);
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
