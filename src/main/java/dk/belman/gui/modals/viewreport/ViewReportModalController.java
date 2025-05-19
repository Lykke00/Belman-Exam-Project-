package dk.belman.gui.modals.viewreport;

import atlantafx.base.theme.Styles;
import com.gluonhq.charm.glisten.control.Dialog;
import com.gluonhq.charm.glisten.control.ProgressIndicator;
import com.gluonhq.charm.glisten.control.TextArea;
import dk.belman.enums.ReportStatus;
import dk.belman.gui.components.FakePDFPreviewPane;
import dk.belman.gui.components.GluonSnackbar;
import dk.belman.gui.components.Snackbar;
import dk.belman.gui.interactors.InteractorManager;
import dk.belman.gui.interactors.ReportInteractor;
import dk.belman.gui.modals.Modal;
import dk.belman.gui.pages.common.PictureItemModel;
import dk.belman.gui.pages.common.ReportItemModel;
import dk.belman.gui.pages.inspector.reportview.ReportItemViewModel;
import dk.belman.gui.utils.BackgroundTaskExecutor;
import dk.belman.gui.utils.DialogHandler;
import dk.belman.gui.utils.ModalHandler;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ViewReportModalController implements Initializable {
    private final ReportInteractor reportInteractor = InteractorManager.getInstance().getReportInteractor();
    private final ReportItemViewModel reportItemViewModel = reportInteractor.getReportItemViewModel();

    @FXML
    private Label lblInspectorComment;

    @FXML
    private Button btnAccept, btnReject, btnAddComment;

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
        lblInspectorComment.textProperty().bind(reportItemModel.inspectorCommentProperty());

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

        btnAddComment.setOnAction(this::btnAddComment);
        btnAddComment.getStyleClass().add("normal-btn");
    }

    private void btnAddComment(ActionEvent event) {
        javafx.scene.control.Dialog<String> dialog = new javafx.scene.control.Dialog<>();
        dialog.setTitle("Add comment");
        dialog.setHeaderText("Add comment");

        dialog.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        dialog.initOwner(btnAddComment.getScene().getWindow());

        javafx.scene.control.TextArea message = new javafx.scene.control.TextArea();
        message.setPromptText("Enter your comment here...");
        message.setPrefHeight(200);
        message.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #ccc; -fx-border-radius: 8px;");
        message.setPadding(new Insets(5));

        message.setWrapText(true);

        VBox content = new VBox(10, message);
        content.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(content);

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        ButtonType closeButtonType = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, closeButtonType);

        dialog.getDialogPane().lookupButton(saveButtonType).getStyleClass().add(Styles.SUCCESS);
        dialog.getDialogPane().lookupButton(closeButtonType).getStyleClass().add(Styles.DANGER);

        Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);

        BooleanBinding isValid = Bindings.createBooleanBinding(() -> {
            String text = message.getText();
            return !text.isEmpty() && text.length() >= 5 && !text.contains("  ") && text.length() <= 255;
        }, message.textProperty());
        saveButton.disableProperty().bind(isValid.not());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                String comment = message.getText();
                if (comment.isEmpty() || comment.length() < 5 || comment.contains("  ")) {
                    GluonSnackbar.showSnackbar("Comment is too short or contains multiple spaces");
                    return null;
                }

                if (comment.length() > 255) {
                    GluonSnackbar.showSnackbar("Comment is too long");
                    return null;
                }

                ReportItemModel report = reportItemViewModel.reportItemModelProperty().get();
                reportInteractor.updateInspectorComment(report, comment, success -> {
                    if (success) {
                        GluonSnackbar.showSnackbar("Comment added successfully");
                    } else {
                        GluonSnackbar.showSnackbar("Failed to add comment");
                    }
                });

                return comment;
            }
            return null;
        });

        Platform.runLater(() -> {
            Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
            stage.setAlwaysOnTop(true);
        });

        dialog.showAndWait();
    }
}
