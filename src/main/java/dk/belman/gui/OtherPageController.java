package dk.belman.gui;

import com.gluonhq.charm.glisten.application.AppManager;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import dk.belman.gui.utils.DialogHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class OtherPageController extends View implements Initializable {

    @FXML
    private Button backBtn;

    public OtherPageController() {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        backBtn.setOnAction(this::handleBackButtonClick);
    }

    private void handleBackButtonClick(ActionEvent actionEvent) {
        AppManager.getInstance().switchToPreviousView();
    }

    @Override
    public void updateAppBar(AppBar appBar) {
        AppManager appManager = AppManager.getInstance();

        appManager.getAppBar().setNavIcon(MaterialDesignIcon.MENU.button(e -> DrawerManager.getInstance().getDrawer().open()));
    }
}
