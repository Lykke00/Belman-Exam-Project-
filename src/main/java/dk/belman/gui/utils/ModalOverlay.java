package easv.event.gui.utils;

import atlantafx.base.controls.ModalPane;
import atlantafx.base.layout.ModalBox;
import atlantafx.base.theme.Styles;
import easv.event.gui.modals.IModalController;
import easv.event.gui.modals.Modal;
import easv.event.gui.pages.IPageController;
import javafx.animation.Animation;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Wrapper for ModalPane to fix animations only happening once during its lifetime.
 */
public class ModalOverlay extends ModalPane {
    private Object controller;

    private final Map<Modal, Node> modalCache = new HashMap<>();

    public ModalOverlay() {
        super();

        // Animate when clicking bg to close
        this.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            Node target = event.getTarget() instanceof Node ? (Node) event.getTarget() : null;
            // Hvis det er en scrollpane og har styleclassen scrollable-content
            boolean isStackPane = target instanceof StackPane;
            if (isStackPane && target.getStyleClass().contains("scrollable-content") ||
                    isStackPane && target.getStyleClass().contains("close-button") ||
                    isStackPane && target.getStyleClass().contains("icon")) {
                event.consume();
                hide();
            }
        });
    }

    @Override
    public void show(Node node) {
        Animation inAnimation = inTransitionFactory.get().apply(node);
        super.show(node);
        inAnimation.play();
    }

    @Override
    public void hide() {
        if (getContent() == null)
            return;

        Animation outAnimation = outTransitionFactory.get().apply(getContent());
        outAnimation.setOnFinished(evt -> super.hide(true));
        outAnimation.play();
    }

    public void showFXML(Modal modal) {
        displayModal(modal);
    }

    private void displayModal(Modal modal) {
        FXMLLoader loader;
        Node modalNode = modalCache.get(modal);

        if (modalNode == null) {
            loader = new FXMLLoader(getClass().getResource(modal.getPath()));
            try {
                modalNode = loader.load();
                modal.setController(loader.getController());
                modalCache.put(modal, modalNode);
            } catch (Exception e) {
                throw new RuntimeException("Failed to load Modal: " + modal.getPath(), e);
            }
        }

        if (modal.getController() instanceof IModalController) {
            ((IModalController) modal.getController()).load();
        }

        ModalBox modalBox = new ModalBox();

        modalBox.getStyleClass().add("modal-box");

        modalBox.addContent(modalNode);
        modalBox.setMaxSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);

        show(modalBox);
    }

    @Override
    public void hide(boolean clear) {
        hide();
    }
}