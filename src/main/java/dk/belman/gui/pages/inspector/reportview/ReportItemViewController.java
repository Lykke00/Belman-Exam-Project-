package dk.belman.gui.pages.inspector.reportview;

import com.gluonhq.charm.glisten.mvc.View;
import dk.belman.gui.interactors.InteractorManager;
import dk.belman.gui.interactors.ReportInteractor;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class ReportItemViewController extends View implements Initializable {
    private final ReportInteractor reportInteractor = InteractorManager.getInstance().getReportInteractor();
    private final ReportItemViewModel viewModel = reportInteractor.getReportItemViewModel();

    @FXML
    private VBox vBoxPreview;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    private void setupPreviewPane() {
        vBoxPreview.getChildren().clear();
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setProgress(-1);
        progressIndicator.setPadding(new Insets(50, 0, 50, 0));

        vBoxPreview.getChildren().add(progressIndicator);

        /*
        BackgroundTaskExecutor.execute(
                () -> FakePDFPreviewPane.createPreviewObservable(viewModel.reportItemModelProperty().getOrderNumber(), "workerName", viewModel.getReportItemModel().getId()),
                menuItem -> {
                    if (vBoxPreview.getChildren().contains(progressIndicator)) {
                        vBoxPreview.getChildren().remove(progressIndicator);
                        vBoxPreview.getChildren().add(menuItem);
                    }
                },
                exception -> {
                    DialogHandler.showExceptionError("Error", "An error occured while generating preview for PDF", exception);
                }
        );*/
    }
}
