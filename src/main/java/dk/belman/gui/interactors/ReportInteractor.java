package dk.belman.gui.interactors;

import dk.belman.be.OperatorReport;
import dk.belman.bll.ReportManager;
import dk.belman.gui.pages.common.ReportItemModel;
import dk.belman.gui.pages.inspector.reportitemview.ReportItemViewModel;
import dk.belman.gui.pages.inspector.reports.ReportModel;
import dk.belman.gui.pages.operator.PictureProcess.PictureProcessModel;
import dk.belman.gui.utils.BackgroundTaskExecutor;
import dk.belman.gui.utils.DialogHandler;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class ReportInteractor {
    private ReportManager reportManager;


    private final ReportModel reportModel;
    private final ReportItemViewModel reportItemViewModel;

    public ReportInteractor() {
        this.reportModel = new ReportModel();
        this.reportItemViewModel = new ReportItemViewModel();

        try {
            this.reportManager = new ReportManager();
        } catch (Exception e) {
            DialogHandler.showExceptionError("Error initializing ReportManager", "Couldn't initialize ReportManager, an unexpected error occurred", e);
        }

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
                    reportItemViewModel.reportItemModelProperty().set(itemModel);
                },
                error -> {
                    DialogHandler.showExceptionError("Error fetching report", "Couldn't fetch report from database, an unexepected error occured", error);
                },
                loading -> {
                    reportItemViewModel.loadedProperty().set(!loading);
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
