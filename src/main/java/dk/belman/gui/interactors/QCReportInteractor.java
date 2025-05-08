package dk.belman.gui.interactors;

import dk.belman.gui.common.QCReportModel;

public class QCReportInteractor {
    private final QCReportModel qcReportModel;

    public QCReportInteractor() {
        this.qcReportModel = new QCReportModel();
    }

    public QCReportModel getQCReportModel() {
        return qcReportModel;
    }
}
