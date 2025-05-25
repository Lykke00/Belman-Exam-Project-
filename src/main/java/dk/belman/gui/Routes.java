package dk.belman.gui;

import com.gluonhq.charm.glisten.application.AppManager;

public class Routes {
    public static void setupViews(AppManager appManager) {
        appManager.addViewFactory(AppView.LOGIN.getRoute(), () -> ViewLoader.load(AppView.LOGIN));

        /* ----- Operator Views ----- */
        appManager.addViewFactory(AppView.OPERATOR_LANDING.getRoute(), () -> ViewLoader.load(AppView.OPERATOR_LANDING));
        appManager.addViewFactory(AppView.OPERATOR_PICTURE_PROCESS.getRoute(), () -> ViewLoader.load(AppView.OPERATOR_PICTURE_PROCESS));
        appManager.addViewFactory(AppView.OPERATOR_PICTURE_FINAL.getRoute(), () -> ViewLoader.load(AppView.OPERATOR_PICTURE_FINAL));

        /* ----- Inspector Views ----- */
        appManager.addViewFactory(AppView.INSPECTOR_VIEW_REPORTS.getRoute(), () -> ViewLoader.load(AppView.INSPECTOR_VIEW_REPORTS));
        appManager.addViewFactory(AppView.INSPECTOR_VIEW_REPORT.getRoute(), () -> ViewLoader.load(AppView.INSPECTOR_VIEW_REPORT));

        /* ----- Admin Views ----- */
        appManager.addViewFactory(AppView.ADMIN_USERS_VIEW.getRoute(), () -> ViewLoader.load(AppView.ADMIN_USERS_VIEW));
    }
}
