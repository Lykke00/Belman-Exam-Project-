package dk.belman.gui.interactors;

import dk.belman.gui.common.QCReportModel;

public class InteractorManager {
    private static InteractorManager instance;

    private final AuthInteractor authInteractor;
    private final PictureProcessInteractor pictureProcessInteractor;

    private InteractorManager() {
        this.authInteractor = new AuthInteractor();
        this.pictureProcessInteractor = new PictureProcessInteractor();
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
}

