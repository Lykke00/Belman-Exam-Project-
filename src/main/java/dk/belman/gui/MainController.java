package dk.belman.gui;

import com.gluonhq.charm.glisten.application.AppManager;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.control.LifecycleEvent;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import dk.belman.gui.utils.DialogHandler;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainController extends View implements Initializable {

    @FXML
    private Button btnClick;

    @FXML
    private Label lbl;

    private int count;

    public MainController() {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnClick.setOnAction(this::handleButtonClick);
    }

    private void handleButtonClick(ActionEvent actionEvent) {
        count++;
        lbl.setText("du har trykket " + count + " gange");
    }

    @Override
    public void updateAppBar(AppBar appBar) {
        AppManager appManager = AppManager.getInstance();

        appManager.getAppBar().setTitle(new Label("Main Page"));
        appManager.getAppBar().setNavIcon(MaterialDesignIcon.MENU.button(e -> DrawerManager.getInstance().getDrawer().open()));
    }
}