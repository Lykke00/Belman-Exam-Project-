package easv.event.gui.utils;

import javafx.scene.layout.StackPane;

public class ModalHandler {
    private static ModalHandler instance;
    private StackPane root;
    private ModalOverlay modalOverlay = new ModalOverlay();

    private ModalHandler() {}

    public static ModalHandler getInstance() {
        if (instance == null)
            instance = new ModalHandler();

        return instance;
    }

    public void setRoot(StackPane root) {
        this.root = root;

        root.getChildren().add(modalOverlay);
    }

    public ModalOverlay getModalOverlay() {
        return modalOverlay;
    }

    public void hideModal() {
        modalOverlay.hide();
    }
}
