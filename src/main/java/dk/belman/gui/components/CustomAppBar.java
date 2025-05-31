package dk.belman.gui.components;

import com.gluonhq.attach.util.Platform;
import com.gluonhq.charm.glisten.application.AppManager;
import com.gluonhq.charm.glisten.application.ViewStackPolicy;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import dk.belman.gui.AppView;
import dk.belman.gui.common.AuthModel;
import dk.belman.gui.common.UserModel;
import dk.belman.gui.interactors.InteractorManager;
import dk.belman.gui.utils.LabelStyle;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class CustomAppBar {
    private final static AuthModel authModel = InteractorManager.getInstance().getAuthInteractor().getAuthModel();

    private static final Logger log = LoggerFactory.getLogger(CustomAppBar.class);

    public static void updateAppBar(AppBar appBar, String title, boolean showPrevious) {
        Image logo = new Image(Objects.requireNonNull(CustomAppBar.class.getResourceAsStream("/images/logo_white.png")));

        ImageView logoView = new ImageView(logo);
        logoView.setFitWidth(25);
        logoView.setFitHeight(25);
        logoView.setPreserveRatio(true);

        HBox logoBox = new HBox(10, logoView, LabelStyle.getAppBarTitle(title));
        logoBox.setAlignment(Pos.CENTER_LEFT);

        appBar.setTitle(logoBox);
        appBar.setStyle("-fx-background-color: #00539B;");

        UserModel userModel = authModel.userProperty().get();

        Label id = new Label();
        id.textProperty().bind(Bindings.concat("#", userModel.workerIdProperty()));

        id.setPadding(new Insets(0, 10, 0, 0));
        id.setStyle("-fx-font-size: 16px; -fx-text-fill: #ededed;");

        Button logOut;

        if (Platform.isDesktop()) {
            logOut = new Button();
            SVGPath svg = new SVGPath();
            svg.setContent("M7 18H4a1 1 0 0 1-1-1V3a1 1 0 0 1 1-1h3 M13 15 L17 10 L13 5 M17 10 L7 10");
            svg.setFill(Color.TRANSPARENT);
            svg.setStroke(Color.WHITE);
            svg.setStrokeWidth(2.5);

            svg.setScaleX(0.8);
            svg.setScaleY(0.8);

            logOut.setGraphic(svg);
            logOut.getStyleClass().add("logout-button-desktop");

            logOut.setOnAction(e -> {
                authModel.logOut(true);
                AppManager.getInstance().switchView(AppView.LOGIN.getRoute(), ViewStackPolicy.CLEAR);
            });
        }
        else {
            logOut = MaterialDesignIcon.EXIT_TO_APP.button(e -> {
                authModel.logOut(true);
                AppManager.getInstance().switchView(AppView.LOGIN.getRoute(), ViewStackPolicy.CLEAR);
            });

            logOut.getStyleClass().add("logout-button");
        }

        HBox hBox = new HBox(5, id, logOut);
        hBox.setAlignment(Pos.CENTER_RIGHT);

        hBox.getStylesheets().add(Objects.requireNonNull(CustomAppBar.class.getResource("/css/belman.css")).toExternalForm());

        appBar.getActionItems().add(hBox);

        if (showPrevious) {
            Button backButton = MaterialDesignIcon.ARROW_BACK.button(e -> AppManager.getInstance().switchToPreviousView());
            appBar.setNavIcon(backButton);
        }
    }
}
