package dk.belman.gui.modals;

public enum Modal {
    REPORT_ITEM_VIEW("/fxml/modals/ViewReportModal.fxml"),
    USER_CREATE_NEW("/fxml/modals/UserCreateNewModal.fxml"),
    USER_EDIT("/fxml/modals/UserEditModal.fxml");

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