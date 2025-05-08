package dk.belman.gui.pages.operator.landing;

import com.gluonhq.attach.pictures.PicturesService;
import com.gluonhq.attach.util.Platform;
import com.gluonhq.charm.glisten.application.AppManager;
import com.gluonhq.charm.glisten.control.*;
import com.gluonhq.charm.glisten.mvc.View;
import dk.belman.gui.Routes;
import dk.belman.gui.common.QCReportModel;
import dk.belman.gui.components.OperatorAppBar;
import dk.belman.gui.components.SelectableImageView;
import dk.belman.gui.interactors.InteractorManager;
import dk.belman.gui.utils.LabelStyle;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class LandingController extends View implements Initializable {
    private final QCReportModel model = InteractorManager.getInstance().getQCReportInteractor().getQCReportModel();

    private PicturesService picturesService;

    private final static int PICTURE_WIDTH = 200;
    private final static int PICTURE_HEIGHT = 200;

    @FXML
    private TextField txtFieldOrderNumber;

    @FXML
    private VBox vBoxMain;

    @FXML
    private Button btnTakePhoto;

    private final TilePane picturePane = new TilePane();

    public LandingController() {

    }


    @Override
    public void updateAppBar(AppBar appBar) {
        OperatorAppBar.updateAppBar(appBar, "Upload Images", false);
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
        // .and(txtFieldOrderNumber.textProperty().length().lessThan(20));

        btnTakePhoto.disableProperty().bind(isValid.not());
    }

    private void setupTakePhotoButton() {
        btnTakePhoto.setOnAction(e -> {
            model.qcReportIdProperty().set(txtFieldOrderNumber.getText());

            model.getImages().clear();
            picturePane.getChildren().clear();

            if (Platform.isDesktop()) {
                takePhotoDesktop();
                showPictures();
            } else {
                takePhotoMobile();
                showPictures();
            }
        });
    }

    private void takePhotoDesktop() {
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icon.png")));
        Image image2 = new Image("file:///C:/Users/mathi/OneDrive%20-%20Family%20business/Desktop/pb/gta/0e92e6bc9e0dea6fe8219e62355d7a3e.jpg");
        Image image3 = new Image("file:///C:/Users/mathi/OneDrive%20-%20Family%20business/Desktop/pb/gta/b24eb6cec0c947981ab87179767412cf.jpg");
        Image image4 = new Image("file:///C:/Users/mathi/OneDrive%20-%20Family%20business/Desktop/pb/gta/f4f8fa688188e83490ec26618b3d267c.jpg");
        Image image5 = new Image("file:///C:/Users/mathi/OneDrive%20-%20Family%20business/Desktop/pb/gta/6a96f3f37956b2c39425f94d721e48c8.jpg");
        Image image6 = new Image("file:///C:/Users/mathi/OneDrive%20-%20Family%20business/Desktop/pb/gta/fa73b7090ade51e87d78607e23fafef0.jpg");

        if (image.isError()) {
            System.err.println("Failed to load image: " + image.getException());
        } else {
            SelectableImageView imageView = new SelectableImageView(image, PICTURE_WIDTH, PICTURE_HEIGHT);
            SelectableImageView imageView2 = new SelectableImageView(image2, PICTURE_WIDTH, PICTURE_HEIGHT);
            SelectableImageView imageView3 = new SelectableImageView(image3, PICTURE_WIDTH, PICTURE_HEIGHT);
            SelectableImageView imageView4 = new SelectableImageView(image4, PICTURE_WIDTH, PICTURE_HEIGHT);
            SelectableImageView imageView5 = new SelectableImageView(image5, PICTURE_WIDTH, PICTURE_HEIGHT);
            SelectableImageView imageView6 = new SelectableImageView(image6, PICTURE_WIDTH, PICTURE_HEIGHT);

            picturePane.getChildren().addAll(imageView, imageView2, imageView3, imageView4, imageView5, imageView6);
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
        btnCreate.getStyleClass().add("take-photo-btn");

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
        model.getImages().clear();
        model.commentProperty().set("");

        InteractorManager.getInstance().getAuthInteractor().getAuthModel().logOut(true);
        AppManager.getInstance().switchView(Routes.LOGIN);
    }
}
