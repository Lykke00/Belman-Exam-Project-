package dk.belman.gui.pages.operator.PictureProcess;

import com.gluonhq.charm.glisten.application.AppManager;
import com.gluonhq.charm.glisten.application.ViewStackPolicy;
import com.gluonhq.charm.glisten.mvc.View;
import dk.belman.gui.AppView;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class PictureProcessController extends View implements Initializable {

    @FXML
    private VBox vBoxPicture;

    @FXML
    private Button btnTakePicture;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnTakePicture.setOnAction(e -> {
            AppManager.getInstance().switchView(AppView.OPERATOR_PICTURE_PROCESS.getRoute());
        });
    }
}
