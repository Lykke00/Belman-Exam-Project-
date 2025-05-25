package dk.belman.gui;

import com.gluonhq.charm.glisten.animation.FadeInDownBigTransition;
import com.gluonhq.charm.glisten.animation.FadeInRightBigTransition;
import com.gluonhq.charm.glisten.animation.MobileTransition;
import com.gluonhq.charm.glisten.mvc.View;

public enum AppView {
    LOGIN("login", "/fxml/pages/loginApp.fxml", FadeInDownBigTransition::new),
    OPERATOR_LANDING("operator_landing", "/fxml/pages/operator/landing.fxml"),
    OPERATOR_PICTURE_PROCESS("operator_pictureprocess", "/fxml/pages/operator/pictureprocess.fxml", FadeInRightBigTransition::new),
    OPERATOR_PICTURE_FINAL("operator_pictureprocessfinal", "/fxml/pages/operator/picturefinal.fxml", FadeInRightBigTransition::new),
    INSPECTOR_VIEW_REPORTS("inspector_viewreports", "/fxml/pages/inspector/reports.fxml", FadeInRightBigTransition::new),
    INSPECTOR_VIEW_REPORT("inspector_viewreport", "/fxml/pages/inspector/reportItemView.fxml"),
    ADMIN_USERS_VIEW("admin_users", "/fxml/pages/admin/users.fxml", FadeInRightBigTransition::new);

    private final String pageRoute;
    private final String fxmlPath;
    private final TransitionFactory transitionFactory;

    AppView(String pageRoute, String fxmlPath) {
        this(pageRoute, fxmlPath, null);
    }

    AppView(String pageRoute, String fxmlPath, TransitionFactory transitionFactory) {
        this.pageRoute = pageRoute;
        this.fxmlPath = fxmlPath;
        this.transitionFactory = transitionFactory;
    }

    public String getRoute() {
        return pageRoute;
    }

    public String getFxmlPath() {
        return fxmlPath;
    }

    public TransitionFactory getTransitionFactory() {
        return transitionFactory;
    }

    @FunctionalInterface
    public interface TransitionFactory {
        MobileTransition create(View view);
    }
}
