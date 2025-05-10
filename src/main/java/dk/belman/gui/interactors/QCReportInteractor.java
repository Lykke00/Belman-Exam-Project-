package dk.belman.gui.interactors;

import dk.belman.be.OperatorReport;
import dk.belman.bll.ReportManager;
import dk.belman.gui.common.QCReportModel;
import dk.belman.gui.pages.operator.PictureProcess.PictureItemModel;
import dk.belman.gui.pages.operator.PictureProcess.PictureProcessModel;
import dk.belman.gui.utils.BackgroundTaskExecutor;
import dk.belman.gui.utils.DialogHandler;

import java.util.function.Consumer;

public class QCReportInteractor {
    private final PictureProcessModel pictureProcessModel;
    private final ReportManager reportManager;

    public QCReportInteractor() {
        this.pictureProcessModel = new PictureProcessModel();
        this.reportManager = new ReportManager();
    }

    public void sendReport(Consumer<Boolean> callback) {
        BackgroundTaskExecutor.execute(
            () -> {
                OperatorReport report = PictureProcessModel.toEntity(pictureProcessModel);
                return reportManager.createReport(report);
            },
            result -> {
                callback.accept(result);
            },
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
