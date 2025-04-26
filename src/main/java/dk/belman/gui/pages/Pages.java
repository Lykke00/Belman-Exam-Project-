package dk.belman.gui.pages;

public enum Pages {
    MAIN("/fxml/main.fxml");

    private final String path;
    private Object controller;

    Pages(String path) {
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
