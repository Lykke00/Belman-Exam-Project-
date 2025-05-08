package dk.belman.gui;

import com.gluonhq.charm.glisten.mvc.View;
import dk.belman.gui.utils.DialogHandler;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.util.Objects;

public class ViewLoader {
    public static View load(AppView appView) {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(ViewLoader.class.getResource(appView.getFxmlPath())));

        try {
            View view = loader.load();

            if (appView.getTransitionFactory() != null)
                view.setShowTransitionFactory(v -> appView.getTransitionFactory().create(v));

            return view;
        } catch (IOException e) {
            DialogHandler.showExceptionError("Error loading view", "Loading FXML view failed", e);
            return new View(); // fallback
        }
    }
}
