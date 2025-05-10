package dk.belman.gui.interactors;

import dk.belman.gui.common.QCReportModel;
import dk.belman.gui.utils.BackgroundTaskExecutor;

public class QCReportInteractor {
    private final QCReportModel qcReportModel;

    public QCReportInteractor() {
        this.qcReportModel = new QCReportModel();
    }

    public void sendReport() {
        BackgroundTaskExecutor.execute(
            () -> {
                // Simulate sending the report
                Thread.sleep(2000); // Simulate network delay
                return true; // Simulate success
            },
            result -> {
                if (result) {
                    qcReportModel.setReportSent(true);
                    System.out.println("Report sent successfully.");
                } else {
                    qcReportModel.setReportSent(false);
                    System.out.println("Failed to send report.");
                }
            },
            error -> {
                qcReportModel.setReportSent(false);
                System.out.println("Error occurred while sending report: " + error.getMessage());
            }
        );
    }

    public QCReportModel getQCReportModel() {
        return qcReportModel;
    }
}
