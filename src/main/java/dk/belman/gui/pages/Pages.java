package easv.event.gui.pages;

public enum Pages {
    MAIN("/fxml/main.fxml"),
    LOGIN("/fxml/pages/LoginPage.fxml"),
    EVENT("/fxml/pages/EventPage.fxml"),
    TICKETS("/fxml/pages/TicketPage.fxml"),
    USERS("/fxml/pages/UserPage.fxml"),
    EVENT_ITEM_PAGE("/fxml/pages/EventItemPage.fxml"),
    TICKET_ITEM_PAGE("/fxml/pages/TicketItemPage.fxml");

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
