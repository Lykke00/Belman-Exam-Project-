package dk.belman.gui.interactors;

public class InteractorManager {
    private static InteractorManager instance;

    private final AuthInteractor authInteractor;
    private final PictureProcessInteractor pictureProcessInteractor;
    private final ReportInteractor reportInteractor;
    private final UserInteractor userInteractor;

    private InteractorManager() {
        this.authInteractor = new AuthInteractor();
        this.pictureProcessInteractor = new PictureProcessInteractor();
        this.reportInteractor = new ReportInteractor();
        this.userInteractor = new UserInteractor();
    }

    public static InteractorManager getInstance() {
        if (instance == null) {
            instance = new InteractorManager();
        }
        return instance;
    }

    public AuthInteractor getAuthInteractor() {
        return authInteractor;
    }

    public PictureProcessInteractor getPictureProcessInteractor() {
        return pictureProcessInteractor;
    }

    public ReportInteractor getReportInteractor() {
        return reportInteractor;
    }

    public UserInteractor getUserInteractor() {
        return userInteractor;
    }

    public void reset() {
        instance = null;
    }
}

