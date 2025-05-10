package dk.belman.gui.interactors;

import dk.belman.be.OperatorReport;
import dk.belman.bll.ReportManager;
import dk.belman.gui.common.QCReportModel;
import dk.belman.gui.pages.operator.PictureProcess.PictureItemModel;
import dk.belman.gui.pages.operator.PictureProcess.PictureProcessModel;
import dk.belman.gui.utils.BackgroundTaskExecutor;
import dk.belman.gui.utils.DialogHandler;

import java.awt.*;
import java.util.function.Consumer;

public class QCReportInteractor {
    private final PictureProcessModel pictureProcessModel;
    private ReportManager reportManager;

    public QCReportInteractor() {
        this.pictureProcessModel = new PictureProcessModel();
        try {
            this.reportManager = new ReportManager();
        } catch (Exception e) {
            DialogHandler.showExceptionError("Error initializing ReportManager", "Couldn't initialize ReportManager, an unexpected error occurred", e);
        }
    }

    public void sendReport(Consumer<Boolean> callback) {
        BackgroundTaskExecutor.executeWithExceptionHandling(
            () -> {
                OperatorReport report = PictureProcessModel.toEntity(pictureProcessModel);
                return reportManager.createReport(report);
            },
                callback,
            error -> {
                DialogHandler.showExceptionError("Error sending report", "Couldn't send report to database, an unexepected error occured", error);
                callback.accept(false);
            },
                loading -> {
                pictureProcessModel.databaseLoadingProperty().set(loading);
            }
        );
    }

    public PictureProcessModel getPictureProcessModel() {
        return pictureProcessModel;
    }
}
