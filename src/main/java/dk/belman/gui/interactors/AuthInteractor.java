package dk.belman.gui.interactors;

import dk.belman.gui.common.AuthModel;
import dk.belman.gui.common.QCReportModel;

public class AuthInteractor {
    private AuthModel authModel;

    public AuthInteractor() {
        // this.authModel = new AuthModel();
    }

    public AuthInteractor(QCReportModel qcReportModel) {
        this.authModel = new AuthModel(qcReportModel);
    }

    public AuthModel getAuthModel() {
        return authModel;
    }
}
