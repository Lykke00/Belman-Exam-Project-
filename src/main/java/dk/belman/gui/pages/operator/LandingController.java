package dk.belman.gui.pages.operator;

import com.gluonhq.charm.glisten.application.AppManager;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import dk.belman.gui.DrawerManager;
import javafx.scene.control.Label;

public class LandingController extends View {

    public LandingController() {}


    @Override
    public void updateAppBar(AppBar appBar) {
        AppManager appManager = AppManager.getInstance();

        appManager.getAppBar().setTitle(new Label("Upload images"));
        appManager.getAppBar().setNavIcon(MaterialDesignIcon.MENU.button(e -> DrawerManager.getInstance().getDrawer().open()));
    }
}
