package dk.belman.gui.interactors;

import dk.belman.gui.pages.operator.PictureProcess.PictureProcessModel;

public class PictureProcessInteractor {
    private final PictureProcessModel model;

    public PictureProcessInteractor() {
        this.model = new PictureProcessModel();
    }

    public PictureProcessModel getModel() {
        return model;
    }
}
