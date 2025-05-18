package dk.belman.gui.modals.viewreport;

import atlantafx.base.theme.Styles;
import com.gluonhq.charm.glisten.control.ProgressIndicator;
import dk.belman.enums.ReportStatus;
import dk.belman.gui.components.FakePDFPreviewPane;
import dk.belman.gui.components.GluonSnackbar;
import dk.belman.gui.components.Snackbar;
import dk.belman.gui.interactors.InteractorManager;
import dk.belman.gui.interactors.ReportInteractor;
import dk.belman.gui.pages.common.PictureItemModel;
import dk.belman.gui.pages.common.ReportItemModel;
import dk.belman.gui.pages.inspector.reportview.ReportItemViewModel;
import dk.belman.gui.utils.BackgroundTaskExecutor;
import dk.belman.gui.utils.DialogHandler;
import dk.belman.gui.utils.ModalHandler;
import javafx.beans.Observable;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ViewReportModalController implements Initializable {
    private final ReportInteractor reportInteractor = InteractorManager.getInstance().getReportInteractor();
    private final ReportItemViewModel reportItemViewModel = reportInteractor.getReportItemViewModel();

    @FXML
    private Button btnAccept, btnReject;

    @FXML
    private Label lblTitle;

    @FXML
    private VBox vBoxPreview;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        vBoxPreview.setMaxHeight(650);
        reportItemViewModel.reportItemModelProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                rebindValues(newVal);
            }
        });

        setupButtons();
    }

    private void rebindValues(ReportItemModel reportItemModel) {
        lblTitle.textProperty().bind(reportItemModel.orderNumberProperty());

        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setProgress(-1);
        progressIndicator.setPadding(new Insets(50, 0, 50, 0));

        vBoxPreview.getChildren().add(progressIndicator);

        StackPane stackPane = new StackPane();

        String orderNumber = reportItemModel.orderNumberProperty().get();
        String workerName = "bernberg";
        ObservableList<PictureItemModel> pictures = reportItemModel.getImages();

        FakePDFPreviewPane fakePDFPreviewPane = FakePDFPreviewPane.createPreview(orderNumber, workerName, pictures);

        stackPane.getChildren().addAll(fakePDFPreviewPane, progressIndicator);

        fakePDFPreviewPane.visibleProperty().bind(fakePDFPreviewPane.isPageLoaded());
        progressIndicator.visibleProperty().bind(fakePDFPreviewPane.isPageLoaded().not());

        vBoxPreview.getChildren().clear();
        vBoxPreview.getChildren().add(stackPane);
    }

    private void setupButtons() {
        btnAccept.getStyleClass().add(Styles.SUCCESS);
        btnReject.getStyleClass().add(Styles.DANGER);

        btnAccept.setOnAction(event -> {
            ReportItemModel reportItemModel = reportItemViewModel.reportItemModelProperty().get();
            reportInteractor.updateReportStatus(reportItemModel, ReportStatus.ACCEPTED, success -> {
                if (success) {
                    GluonSnackbar.showSnackbar("Report accepted successfully");
                    ModalHandler.getInstance().hideModal();
                }
            });
        });

        btnReject.setOnAction(event -> {
            ReportItemModel reportItemModel = reportItemViewModel.reportItemModelProperty().get();
            reportInteractor.updateReportStatus(reportItemModel, ReportStatus.REJECTED, success -> {
                if (success) {
                    GluonSnackbar.showSnackbar("Report rejected successfully");
                    ModalHandler.getInstance().hideModal();
                }
            });
        });
    }
}
