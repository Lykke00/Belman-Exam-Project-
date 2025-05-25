package dk.belman.gui.interactors;

import dk.belman.bll.ReportManager;
import dk.belman.enums.ReportStatus;
import dk.belman.gui.common.ReportItemModel;
import dk.belman.gui.pages.inspector.reportview.ReportItemViewModel;
import dk.belman.gui.pages.inspector.reports.ReportModel;
import dk.belman.gui.utils.BackgroundTaskExecutor;
import dk.belman.gui.utils.DialogHandler;

import java.util.List;
import java.util.function.Consumer;

public class ReportInteractor {
    private ReportManager reportManager;

    private ReportModel reportModel;
    private ReportItemViewModel reportItemViewModel;

    public ReportInteractor() {
        try {
            this.reportManager = new ReportManager();
        } catch (Exception e) {
            DialogHandler.showExceptionError("Error initializing ReportManager", "Couldn't initialize ReportManager, an unexpected error occurred", e);
        }

        initialize();
    }

    public void initialize() {
        this.reportModel = new ReportModel();
        this.reportItemViewModel = new ReportItemViewModel();

        loadReports();
    }

    private void loadReports() {
        BackgroundTaskExecutor.executeWithExceptionHandling(
                () -> reportManager.getAll(),
                reports -> {
                    System.out.println(reports.size());
                    List<ReportItemModel> reportModels = reports.stream()
                            .map(ReportItemModel::fromEntity)
                            .toList();

                    reportModel.reportsProperty().setAll(reportModels);
                },
                error -> {
                    DialogHandler.showExceptionError("Error sending report", "Couldn't send report to database, an unexepected error occured", error);
                },
                loading -> {
                    reportModel.loadedProperty().set(!loading);
                }
        );
    }

    public void loadReport(ReportItemModel report) {
        BackgroundTaskExecutor.executeWithExceptionHandling(
                () -> reportManager.getReport(report),
                reportItemModel -> {
                    ReportItemModel itemModel = ReportItemModel.fromEntity(reportItemModel);

                    report.setImages(itemModel.getImages());
                    reportItemViewModel.setReportItemModel(report);
                },
                error -> {
                    DialogHandler.showExceptionError("Error fetching report", "Couldn't fetch report from database, an unexepected error occured", error);
                },
                loading -> {
                    reportItemViewModel.loadedProperty().set(!loading);
                }
        );
    }

    public void updateReportStatus(ReportItemModel reportItemModel, ReportStatus status, Consumer<Boolean> callback) {
        BackgroundTaskExecutor.executeWithExceptionHandling(
                () -> reportManager.updateReportStatus(ReportItemModel.toEntity(reportItemModel), status),
                updated -> {
                    if (updated) {
                        reportItemModel.statusProperty().set(status);
                    }
                    callback.accept(updated);
                },
                error -> {
                    callback.accept(false);
                    DialogHandler.showExceptionError("Error changing report status", "Couldn't change report status, an unexpected error occurred", error);
                },
                loading -> {
                    reportItemViewModel.updatingProperty().set(!loading);
                }
        );
    }

    public void updateInspectorComment(ReportItemModel reportItemModel, String comment, Consumer<Boolean> callback) {
        BackgroundTaskExecutor.executeWithExceptionHandling(
                () -> reportManager.updateInspectorComment(ReportItemModel.toEntity(reportItemModel), comment),
                updated -> {
                    if (updated) {
                        reportItemModel.inspectorCommentProperty().set(comment);
                    }
                    callback.accept(updated);
                },
                error -> {
                    callback.accept(false);
                    DialogHandler.showExceptionError("Error updating inspector comment", "Couldn't update inspector comment, an unexpected error occurred", error);
                },
                loading -> {
                    reportItemViewModel.inspectorUpdatingProperty().set(!loading);
                }
        );
    }

    public ReportModel getReportModel() {
        return reportModel;
    }

    public ReportItemViewModel getReportItemViewModel() {
        return reportItemViewModel;
    }
}
