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

            final NavigationDrawer.ViewItem primaryItem = new NavigationDrawer.ViewItem("Home", MaterialDesignIcon.HOME.graphic(), Routes.HOME);
            final NavigationDrawer.ViewItem secondaryItem = new NavigationDrawer.ViewItem("Other Page", MaterialDesignIcon.DASHBOARD.graphic(), Routes.OTHER_PAGE);
            final NavigationDrawer.ViewItem qcPreviewer = new NavigationDrawer.ViewItem("QC Preview", MaterialDesignIcon.DASHBOARD.graphic(), Routes.OPERATOR_QC_PREVIEW);

            drawer.getItems().addAll(primaryItem, secondaryItem, qcPreviewer);

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
