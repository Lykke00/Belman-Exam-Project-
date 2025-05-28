package dk.belman.gui.pages.operator.pictureprocess;

import com.gluonhq.attach.pictures.PicturesService;
import com.gluonhq.attach.util.Platform;
import com.gluonhq.charm.glisten.application.AppManager;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.mvc.View;
import dk.belman.gui.AppView;
import dk.belman.gui.components.CustomAppBar;
import dk.belman.gui.interactors.InteractorManager;
import dk.belman.gui.common.PictureItemModel;
import dk.belman.gui.utils.DialogHandler;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;

import java.net.URL;
import java.util.*;

public class PictureProcessController extends View implements Initializable {
    private final PictureProcessModel model = InteractorManager.getInstance().getPictureProcessInteractor().getModel();
    private PicturesService picturesService;

    @FXML
    private Button btnTakePicture;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupBindings();
    }

    private void setupBindings() {
        btnTakePicture.setOnAction(this::btnNext);
        btnTakePicture.textProperty().bind(
                Bindings.createStringBinding(
                        () -> model.stateProperty().get().textProperty(),
                        model.stateProperty()
                )
        );
    }

    @Override
    public void updateAppBar(AppBar appBar) {
        CustomAppBar.updateAppBar(appBar, "Upload report", false);
    }

    private void btnNext(ActionEvent event) {
        CurrentStateProcess currentPage = model.stateProperty().get();
        PictureItemModel page = model.getStateList().get(currentPage);

        boolean isSendPage = currentPage == CurrentStateProcess.FINISH;

        if (!isSendPage) {
            handleTakePicture(page, currentPage);
            return;
        }

        AppManager.getInstance().switchView(AppView.OPERATOR_PICTURE_FINAL.getRoute());
    }

    private void handleTakePicture(PictureItemModel page, CurrentStateProcess currentPage) {
        Image image = page.pictureProperty().get();
        if (image != null) {
            model.stateProperty().set(CurrentStateProcess.nextState(currentPage));
            return;
        }

        if (Platform.isDesktop()) {
            takePhotoDesktop(page);
        } else {
            takePhotoMobile(page);
        }
    }

    private void takePhotoDesktop(PictureItemModel page) {
       // Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icon.png")));
        Image image = new Image("file:///Users/lykkebernberg/Desktop/den%20sexet%20mand%20patrick/eksempler/4.jpg");

        updateImage(page, image);
    }

    private void takePhotoMobile(PictureItemModel page) {
        if (picturesService == null) {
            picturesService = PicturesService.create().orElse(null);
            if (picturesService == null) return;
        }

        ChangeListener<Image> listener = new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends Image> obs, Image oldImage, Image newImage) {
                picturesService.imageProperty().removeListener(this);
                updateImage(page, newImage);
            }
        };

        picturesService.imageProperty().addListener(listener);
        picturesService.asyncTakePhoto(false);
    }

    private void updateImage(PictureItemModel page, Image image) {
        if (image == null || image.isError()) {
            DialogHandler.showExceptionError("Error loading image", "An error occurred processing taken picture", image.getException());
            return;
        }

        page.pictureProperty().set(image);
        page.stateProperty().set(CurrentStateProcess.nextState(model.stateProperty().get()));

        model.stateProperty().set(CurrentStateProcess.nextState(model.stateProperty().get()));
    }
}
