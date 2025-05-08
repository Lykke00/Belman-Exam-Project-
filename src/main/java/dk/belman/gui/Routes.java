package dk.belman.gui;

import com.gluonhq.charm.glisten.application.AppManager;

public class Routes {
    public static void setupViews(AppManager appManager) {
        appManager.addViewFactory(AppView.LOGIN.getRoute(), () -> ViewLoader.load(AppView.LOGIN));
        appManager.addViewFactory(AppView.OPERATOR_LANDING.getRoute(), () -> ViewLoader.load(AppView.OPERATOR_LANDING));
        appManager.addViewFactory(AppView.OPERATOR_PICTURE_PROCESS.getRoute(), () -> ViewLoader.load(AppView.OPERATOR_PICTURE_PROCESS));
    }
}
