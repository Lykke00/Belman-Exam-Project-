package dk.belman.gui.components;

import atlantafx.base.theme.Styles;
import com.gluonhq.attach.util.Platform;
import com.gluonhq.charm.glisten.application.AppManager;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import dk.belman.gui.AppView;
import dk.belman.gui.interactors.InteractorManager;
import dk.belman.gui.utils.LabelStyle;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomAppBar {

    private static final Logger log = LoggerFactory.getLogger(CustomAppBar.class);

    public static void updateAppBar(AppBar appBar, String title, boolean showPrevious) {
        appBar.setTitle(LabelStyle.getAppBarTitle(title));

        Label id = new Label("#123-456");
        id.setPadding(new Insets(0, 10, 0, 0));
        id.setStyle("-fx-font-size: 16px; -fx-text-fill: #ededed;");

        Button logOut;

        if (Platform.isDesktop()) {
            // fucking lorte knap
            logOut = new Button(null, new FontIcon(Feather.LOG_OUT));
            logOut.getStyleClass().addAll(Styles.BUTTON_CIRCLE, Styles.FLAT, Styles.DANGER);
            logOut.setOnAction(e -> {
                InteractorManager.getInstance().getAuthInteractor().getAuthModel().logOut(true);
                AppManager.getInstance().switchView(AppView.LOGIN.getRoute());
            });
        }
        else {
            logOut = MaterialDesignIcon.EXIT_TO_APP.button(e -> {
                InteractorManager.getInstance().getAuthInteractor().getAuthModel().logOut(true);
                AppManager.getInstance().switchView(AppView.LOGIN.getRoute());
            });

            logOut.getStyleClass().add("logout-button");
        }

        HBox hBox = new HBox(5, id, logOut);
        hBox.setAlignment(Pos.CENTER_RIGHT);

        hBox.getStylesheets().add(CustomAppBar.class.getResource("/css/belman.css").toExternalForm());

        appBar.getActionItems().add(hBox);

        if (showPrevious) {
            Button backButton = MaterialDesignIcon.ARROW_BACK.button(e -> AppManager.getInstance().switchToPreviousView());
            appBar.setNavIcon(backButton);
        }

        //appBar.setNavIcon(MaterialDesignIcon.MENU.button(e -> DrawerManager.getInstance().getDrawer().open()));
    }
}
