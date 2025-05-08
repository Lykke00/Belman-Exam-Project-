package dk.belman.gui.components;

import com.gluonhq.charm.glisten.control.Dialog;

public class ResizableDialog<T> extends Dialog<T> {

    public ResizableDialog(String title) {
        super(title);
        setupSize();
    }

    private void setupSize() {
        rootNode.setStyle("-fx-min-width: 1000px; -fx-min-height: 800px; -fx-pref-width: 1000px; -fx-pref-height: 800px;");
    }

    public void setDialogSize(double width, double height) {
        rootNode.setStyle(String.format("-fx-min-width: %.0fpx; -fx-min-height: %.0fpx; -fx-pref-width: %.0fpx; -fx-pref-height: %.0fpx;",
                width, height, width, height));
    }
}
