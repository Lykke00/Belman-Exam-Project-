package dk.belman.gui.pages.operator.PictureProcess;

import com.gluonhq.charm.glisten.application.AppManager;
import com.gluonhq.charm.glisten.application.ViewStackPolicy;
import com.gluonhq.charm.glisten.mvc.View;
import dk.belman.gui.AppView;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        CurrentStateProcess currentPage = model.stateProperty().get();
        PictureItemModel page = model.getStateList().get(currentPage);

       // page.commentProperty().set("asdasdasdasd");
     //   page.pictureProperty().set(new Image());
     //   page.stateProperty().set(curretPage);

        model.stateProperty().set(CurrentStateProcess.nextState(currentPage));
    }
}
