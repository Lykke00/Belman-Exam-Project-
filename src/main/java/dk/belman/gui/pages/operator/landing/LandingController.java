package dk.belman.gui.pages.operator.landing;

import com.gluonhq.attach.pictures.PicturesService;
import com.gluonhq.attach.util.Platform;
import com.gluonhq.charm.glisten.application.AppManager;
import com.gluonhq.charm.glisten.control.*;
import com.gluonhq.charm.glisten.mvc.View;
import dk.belman.gui.AppView;
import dk.belman.gui.components.CustomAppBar;
import dk.belman.gui.components.SelectableImageView;
import dk.belman.gui.interactors.InteractorManager;
import dk.belman.gui.pages.operator.pictureprocess.CurrentStateProcess;
import dk.belman.gui.pages.operator.pictureprocess.PictureProcessModel;
import dk.belman.gui.utils.LabelStyle;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class LandingController extends View implements Initializable {
    private final PictureProcessModel model = InteractorManager.getInstance().getPictureProcessInteractor().getModel();

    private PicturesService picturesService;

    private final static int PICTURE_WIDTH = 200;
    private final static int PICTURE_HEIGHT = 200;

    @FXML
    private TextField txtFieldOrderNumber;

    @FXML
    private VBox vBoxMain;

    @FXML
    private Button btnBegin;

    private final TilePane picturePane = new TilePane();

    public LandingController() {

    }


    @Override
    public void updateAppBar(AppBar appBar) {
        CustomAppBar.updateAppBar(appBar, "Upload Images", false);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTakePhotoButton();
        validate();
    }

    private void validate() {
        BooleanBinding isValid = txtFieldOrderNumber.textProperty()
                .isNotEmpty()
                .and(txtFieldOrderNumber.textProperty().length().greaterThan(0));

        btnBegin.disableProperty().bind(isValid.not());
    }

    private void setupTakePhotoButton() {
        btnBegin.setOnAction(e -> {
            model.qcReportIdProperty().set(txtFieldOrderNumber.getText());
            model.stateProperty().set(CurrentStateProcess.nextState(model.stateProperty().get()));

            AppManager.getInstance().switchView(AppView.OPERATOR_PICTURE_PROCESS.getRoute());
        });
    }

    private void takePhotoDesktop() {
        Image image1 = new Image("file:///Users/lykkebernberg/Desktop/testbilleder/eksempler/1.jpg");
        Image image2 = new Image("file:///Users/lykkebernberg/Desktop/testbilleder/eksempler/2.jpg");
        Image image3 = new Image("file:///Users/lykkebernberg/Desktop/testbilleder/eksempler/3.jpeg");
        Image image4 = new Image("file:///Users/lykkebernberg/Desktop/testbilleder/eksempler/4.jpg");
        Image image5 = new Image("file:///Users/lykkebernberg/Desktop/testbilleder/eksempler/5.png");

        if (image1.isError()) {
            System.err.println("Failed to load image: " + image1.getException());
        } else {
            SelectableImageView imageView1 = new SelectableImageView(image1, PICTURE_WIDTH, PICTURE_HEIGHT);
            SelectableImageView imageView2 = new SelectableImageView(image2, PICTURE_WIDTH, PICTURE_HEIGHT);
            SelectableImageView imageView3 = new SelectableImageView(image3, PICTURE_WIDTH, PICTURE_HEIGHT);
            SelectableImageView imageView4 = new SelectableImageView(image4, PICTURE_WIDTH, PICTURE_HEIGHT);
            SelectableImageView imageView5 = new SelectableImageView(image5, PICTURE_WIDTH, PICTURE_HEIGHT);

            picturePane.getChildren().addAll(imageView1, imageView2, imageView3, imageView4, imageView5);
        }
    }

    private void takePhotoMobile() {
        if (picturesService == null) {
            picturesService = PicturesService.create().orElse(null);
            if (picturesService == null) return;

            picturesService.imageProperty().addListener((obs, ov, image) -> {
                if (image != null) {
                    SelectableImageView imageView = new SelectableImageView(image, PICTURE_WIDTH, PICTURE_HEIGHT);
                    picturePane.getChildren().add(imageView);
                }
            });
        }

        picturesService.asyncTakePhoto(false);
    }

    private Dialog<String> dialog = new Dialog<>(true);

    private void showPictures() {
        dialog.setTitle(LabelStyle.getAppBarTitle("Add Pictures"));

        picturePane.setHgap(15);
        picturePane.setVgap(15);
        picturePane.setPadding(new Insets(15));
        picturePane.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #ccc; -fx-border-radius: 8px;");
        picturePane.setPrefWidth(Double.MAX_VALUE);

        picturePane.setPrefTileWidth(PICTURE_HEIGHT);
        picturePane.setPrefTileHeight(PICTURE_WIDTH);

        HBox buttonBox = new HBox(10);
        buttonBox.setMinWidth(0);
        buttonBox.setMinHeight(0);

        buttonBox.setPadding(new Insets(10));
        buttonBox.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #ccc; -fx-border-radius: 8px;");
        buttonBox.setAlignment(Pos.CENTER);

        Button btnPhoto = new Button("Take Photo");
        Button btnCreate = new Button("Send QC Report");

        btnPhoto.setOnAction(e -> {
            if (Platform.isDesktop())
                takePhotoDesktop();
            else
                takePhotoMobile();
        });

        btnCreate.setOnAction(this::sendQCReport);

        buttonBox.getStylesheets().add(getClass().getResource("/css/belman.css").toExternalForm());

        btnPhoto.getStyleClass().add("generate-qc-report-btn");
        btnCreate.getStyleClass().add("begin-operator-btn");

        btnPhoto.setMinWidth(0);
        btnCreate.setMinWidth(0);

        btnPhoto.setPrefWidth(400);
        btnCreate.setPrefWidth(400);

        btnPhoto.setPrefHeight(200);
        btnCreate.setPrefHeight(200);

        btnPhoto.setMinHeight(0);
        btnCreate.setMinHeight(0);

        buttonBox.getChildren().addAll(btnPhoto, btnCreate);

        Label label = new Label("title");
        label.setStyle("-fx-font-size: 36px; -fx-font-weight: bold;");
        label.textProperty().bind(model.qcReportIdProperty());

        VBox content = new VBox(10, label, picturePane, buttonBox);

        content.setAlignment(Pos.TOP_CENTER);
        content.setStyle("-fx-padding: 5; -fx-background-radius: 8px; -fx-border-radius: 8px;");

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

        dialog.setContent(scrollPane);

        dialog.showAndWait();
    }


    private void sendQCReport(ActionEvent actionEvent) {
        InteractorManager.getInstance().getAuthInteractor().getAuthModel().logOut(true);
        AppManager.getInstance().switchView(AppView.LOGIN.getRoute());
    }
}
