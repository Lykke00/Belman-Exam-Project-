package dk.belman.gui.interactors;

public class InteractorManager {
    private static InteractorManager instance;

    private final AuthInteractor authInteractor;
    private final PictureProcessInteractor pictureProcessInteractor;
    private final ReportInteractor reportInteractor;
    private final UserInteractor userInteractor;
    private final EmailInteractor emailInteractor;

    private InteractorManager() {
        this.authInteractor = new AuthInteractor();
        this.pictureProcessInteractor = new PictureProcessInteractor();
        this.reportInteractor = new ReportInteractor();
        this.userInteractor = new UserInteractor();
        this.emailInteractor = new EmailInteractor();
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

    public EmailInteractor getEmailInteractor() {
        return emailInteractor;
    }

    public void reset() {
        instance = null;
    }
}

