package easv.event.gui.modals;

public enum Modal {
    EVENT_ADD_NEW("/fxml/modals/AddNewEvent.fxml"),
    EVENT_EDIT("/fxml/modals/EditEvent.fxml"),
    EVENT_ASSIGN("/fxml/modals/EventAssign.fxml"),
    TICKET_ADD_NEW("/fxml/modals/AddNewTicket.fxml"),
    TICKET_ADD_NEW_TYPE("/fxml/modals/AddNewTicketType.fxml"),
    TICKET_EDIT("/fxml/modals/EditTicket.fxml"),
    TICKET_EDIT_TYPE("/fxml/modals/EditTicketType.fxml"),
    TICKET_ADD_EVENT("/fxml/modals/TicketAddEvent.fxml"),
    TICKET_EDIT_EVENT("/fxml/modals/TicketEditEvent.fxml"),
    USERS("/fxml/pages/UserPage.fxml"),
    USER_ADD_NEW("/fxml/modals/UserAddNew.fxml"),
    USER_EDIT("/fxml/modals/UserEdit.fxml"),
    EVENT_SELL_TICKET("/fxml/modals/SellTicketEvent.fxml");

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