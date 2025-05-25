package dk.belman.gui.interactors;

import dk.belman.be.OperatorReport;
import dk.belman.bll.ReportManager;
import dk.belman.gui.pages.operator.pictureprocess.PictureProcessModel;
import dk.belman.gui.utils.BackgroundTaskExecutor;
import dk.belman.gui.utils.DialogHandler;

import java.util.function.Consumer;

public class PictureProcessInteractor {
    private PictureProcessModel model;
    private ReportManager reportManager;

    public PictureProcessInteractor() {
        initialize();

        try {
            this.reportManager = new ReportManager();
        } catch (Exception e) {
            DialogHandler.showExceptionError("Error initializing ReportManager", "Couldn't initialize ReportManager, an unexpected error occurred", e);
        }
    }

    public void initialize() {
        this.model = new PictureProcessModel();
    }

    public void sendReport(Consumer<Boolean> callback) {
        BackgroundTaskExecutor.executeWithExceptionHandling(
                () -> {
                    OperatorReport report = PictureProcessModel.toEntity(model);
                    return reportManager.createReport(report);
                },
                result -> {
                    callback.accept(true);
                },
                error -> {
                    DialogHandler.showExceptionError("Error sending report", "Couldn't send report to database, an unexepected error occured", error);
                    callback.accept(false);
                },
                loading -> {
                    model.databaseLoadingProperty().set(loading);
                }
        );
    }

    public PictureProcessModel getModel() {
        return model;
    }
}
