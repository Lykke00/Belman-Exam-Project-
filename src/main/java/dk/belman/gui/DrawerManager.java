package dk.belman.gui;

import com.gluonhq.attach.lifecycle.LifecycleService;
import com.gluonhq.attach.util.Platform;
import com.gluonhq.attach.util.Services;
import com.gluonhq.charm.glisten.application.AppManager;
import com.gluonhq.charm.glisten.control.Avatar;
import com.gluonhq.charm.glisten.control.NavigationDrawer;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import javafx.scene.image.Image;

import java.util.Objects;

public class DrawerManager {

    private static DrawerManager instance;

    private NavigationDrawer drawer;

    private DrawerManager() {}

    public static DrawerManager getInstance() {
        if (instance == null) {
            instance = new DrawerManager();
            instance.buildDrawer(AppManager.getInstance());
        }
        return instance;
    }

    public void buildDrawer(AppManager app) {
        if (drawer == null) {
            drawer = app.getDrawer();

            NavigationDrawer.Header header = new NavigationDrawer.Header("Belsign",
                    "QR Report System",
                    new Avatar(21, new Image(Objects.requireNonNull(DrawerManager.class.getResourceAsStream("/icon.png")))));
            drawer.setHeader(header);


            drawer.getHeader().setStyle("-fx-background-color: #7A9897; -fx-text-fill: white;");

            final NavigationDrawer.ViewItem operatorLanding = new NavigationDrawer.ViewItem("Landing", MaterialDesignIcon.HOME.graphic(), Routes.OPERATOR_LANDING);
            final NavigationDrawer.ViewItem operatorPreview = new NavigationDrawer.ViewItem("Preview", MaterialDesignIcon.DASHBOARD.graphic(), Routes.OPERATOR_QC_PREVIEW);

            drawer.getItems().addAll(operatorLanding, operatorPreview);

            if (Platform.isDesktop()) {
                final NavigationDrawer.Item quitItem = new NavigationDrawer.Item("Quit", MaterialDesignIcon.EXIT_TO_APP.graphic());
                quitItem.selectedProperty().addListener((obs, ov, nv) -> {
                    if (nv) {
                        Services.get(LifecycleService.class).ifPresent(LifecycleService::shutdown);
                    }
                });
                drawer.getItems().add(quitItem);
            }
        }
    }

    public NavigationDrawer getDrawer() {
        return drawer;
    }

}
