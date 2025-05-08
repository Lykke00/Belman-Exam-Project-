package dk.belman.gui.pages.operator.PictureProcess;

import com.gluonhq.charm.glisten.application.AppManager;
import com.gluonhq.charm.glisten.application.ViewStackPolicy;
import com.gluonhq.charm.glisten.mvc.View;
import dk.belman.gui.AppView;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class PictureProcessController extends View implements Initializable {

    private final PictureProcessModel model = new PictureProcessModel();

    @FXML
    private VBox vBoxPicture;

    @FXML
    private Button btnTakePicture;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnNext();
        setupBindings();
    }

    private void setupBindings() {
        btnTakePicture.textProperty().bind(
                Bindings.createStringBinding(
                        () -> model.stateProperty().get().textProperty(),
                        model.stateProperty()
                )
        );
    }

    private void btnNext() {
        model.stateProperty().set(CurrentStateProcess.nextState(model.stateProperty().get()));
    }
}
