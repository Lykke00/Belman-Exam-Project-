package dk.belman.gui.pages.operator.pictureprocess;

import com.gluonhq.charm.glisten.application.AppManager;
import com.gluonhq.charm.glisten.application.ViewStackPolicy;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.mvc.View;
import dk.belman.gui.AppView;
import dk.belman.gui.common.AuthModel;
import dk.belman.gui.components.CustomAppBar;
import dk.belman.gui.components.GluonSnackbar;
import dk.belman.gui.components.OperatorPicture;
import dk.belman.gui.interactors.InteractorManager;
import dk.belman.gui.common.PictureItemModel;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.ResourceBundle;

public class PictureFinalController extends View implements Initializable {
    private final PictureProcessModel model = InteractorManager.getInstance().getPictureProcessInteractor().getModel();

    @FXML
    private ScrollPane scrollPanePictures;

    @FXML
    private HBox hBoxPicturesContainer;

    @FXML
    private Button btnSend;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupBindings();

        scrollPanePictures.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        hBoxPicturesContainer.getChildren().clear();

        updatePictures();
        sendBtnSetup();
    }

    @Override
    public void updateAppBar(AppBar appBar) {
        CustomAppBar.updateAppBar(appBar, "Confirm report", false);
    }

    private void sendBtnSetup() {
        btnSend.disableProperty().bind(model.databaseLoadingProperty());
        btnSend.setOnAction(this::sendReport);

        btnSend.textProperty().bind(
                Bindings.when(model.databaseLoadingProperty())
                        .then("Loading...")
                        .otherwise("Send")
        );

    }

    private void setupBindings() {
        model.stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == CurrentStateProcess.FINISH) {
                hBoxPicturesContainer.getChildren().clear();
                updatePictures();
            }
        });
    }

    private void updatePictures() {
        for (CurrentStateProcess state : model.getStateList().keySet()) {
            PictureItemModel page = model.getStateList().get(state);
            Image image = page.pictureProperty().get();

            if (image == null)
                continue;

            OperatorPicture operatorPicture = new OperatorPicture(image, state.textProperty(), 125, 125);

            page.commentProperty().bind(operatorPicture.getComment());
            hBoxPicturesContainer.getChildren().add(operatorPicture);
        }
    }

    private void sendReport(ActionEvent actionEvent) {
        String reportId = model.qcReportIdProperty().get();
        if (reportId == null) {
            GluonSnackbar.showSnackbar("No report ID found");
            return;
        }

        InteractorManager.getInstance().getPictureProcessInteractor().sendReport(success -> {
            if (success) {
                GluonSnackbar.showSnackbar("Report sent successfully");
                AuthModel authModel = InteractorManager.getInstance().getAuthInteractor().getAuthModel();
                authModel.logOut(true);
                AppManager.getInstance().switchView(AppView.LOGIN.getRoute(), ViewStackPolicy.CLEAR);
            }
        });
    }
}
