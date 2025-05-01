package dk.belman.gui.pages;

import com.gluonhq.charm.glisten.application.AppManager;
import com.gluonhq.charm.glisten.control.TextField;
import com.gluonhq.charm.glisten.mvc.View;
import dk.belman.gui.Routes;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController extends View implements Initializable {


    @FXML
    private TextField txtFieldUsername, txtFieldPassword;

    @FXML
    private Button btnLogin;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        validate();
        onLogin();
        setupEnterKeyHandler();
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
