package dk.belman.gui.components;

import com.gluonhq.charm.glisten.application.AppManager;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import dk.belman.gui.AppView;
import dk.belman.gui.Routes;
import dk.belman.gui.interactors.InteractorManager;
import dk.belman.gui.utils.LabelStyle;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class OperatorAppBar {

    public static void updateAppBar(AppBar appBar, String title, boolean showPrevious) {
        appBar.setTitle(LabelStyle.getAppBarTitle(title));

        Label id = new Label("#123-456");
        id.setPadding(new Insets(0, 10, 0, 0));
        id.setStyle("-fx-font-size: 16px; -fx-text-fill: #ededed;");

        Button logOut = MaterialDesignIcon.EXIT_TO_APP.button(e -> {
            InteractorManager.getInstance().getQCReportInteractor().getQCReportModel().reset();
            InteractorManager.getInstance().getAuthInteractor().getAuthModel().logOut(true);
            AppManager.getInstance().switchView(AppView.LOGIN.getRoute());
        });

        logOut.getStyleClass().add("logout-button");

        HBox hBox = new HBox(5, id, logOut);
        hBox.setAlignment(Pos.CENTER_RIGHT);

        hBox.getStylesheets().add(OperatorAppBar.class.getResource("/css/belman.css").toExternalForm());

        appBar.getActionItems().add(hBox);

        if (showPrevious) {
            Button backButton = MaterialDesignIcon.ARROW_BACK.button(e -> AppManager.getInstance().switchToPreviousView());
            appBar.setNavIcon(backButton);
        }

        //appBar.setNavIcon(MaterialDesignIcon.MENU.button(e -> DrawerManager.getInstance().getDrawer().open()));
    }
}
