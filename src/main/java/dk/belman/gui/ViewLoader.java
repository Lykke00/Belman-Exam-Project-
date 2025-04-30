package dk.belman.gui;

import com.gluonhq.charm.glisten.mvc.View;
import dk.belman.gui.utils.DialogHandler;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.util.Objects;

public class ViewLoader {
    public static View loadView(String fxmlPath) {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(ViewLoader.class.getResource(fxmlPath)));
        try {
            return loader.load();
        } catch (IOException e) {
            DialogHandler.showExceptionError("Error loading view", "Loading FXML view failed", e);
            return new View();
        }
    }

    public static View loadMainView() {
        return loadView("/fxml/pages/operator/landing.fxml");
    }

    public static View loadOtherPageView() {
        return loadView("/fxml/pages/other-page.fxml");
    }

    public static View loadQCPreviewView() {
        return loadView("/fxml/pages/operator/qcpreview.fxml");
    }
}
