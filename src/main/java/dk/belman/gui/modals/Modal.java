package dk.belman.gui.modals;

public enum Modal {
    REPORT_ITEM_VIEW("/fxml/modals/ViewReportModal.fxml");

    private final String path;
    private Object controller;

    Modal(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setController(Object controller) {
        this.controller = controller;
    }

    public Object getController() {
        return controller;
    }
}