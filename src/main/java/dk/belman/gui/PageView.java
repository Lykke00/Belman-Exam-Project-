package dk.belman.gui;

import com.gluonhq.charm.glisten.application.AppManager;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;

public class PageView extends View {

    public void switchView(String route) {
        AppManager appManager = AppManager.getInstance();
        appManager.switchView(route);
        appManager.getAppBar().setNavIcon(MaterialDesignIcon.MENU.button(e -> {
            getAppManager().getDrawer().open();
        }));

        updateAppBar(appManager.getAppBar());
    }
}
