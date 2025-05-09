package dk.belman.gui.interactors;

public class InteractorManager {
    private static InteractorManager instance;

    private final QCReportInteractor qcReportInteractor;
    private final AuthInteractor authInteractor;
    private final PictureProcessInteractor pictureProcessInteractor;

    private InteractorManager() {
        this.qcReportInteractor = new QCReportInteractor();
        this.authInteractor = new AuthInteractor(qcReportInteractor.getQCReportModel());
        this.pictureProcessInteractor = new PictureProcessInteractor();
    }

    public static InteractorManager getInstance() {
        if (instance == null) {
            instance = new InteractorManager();
        }
        return instance;
    }

    public QCReportInteractor getQCReportInteractor() {
        return qcReportInteractor;
    }

    public AuthInteractor getAuthInteractor() {
        return authInteractor;
    }

    public PictureProcessInteractor getPictureProcessInteractor() {
        return pictureProcessInteractor;
    }
}

