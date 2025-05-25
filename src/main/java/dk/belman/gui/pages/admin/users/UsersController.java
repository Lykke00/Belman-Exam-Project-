package dk.belman.gui.pages.admin.users;

import atlantafx.base.theme.Styles;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.mvc.View;
import dk.belman.enums.UserRole;
import dk.belman.gui.common.ReportItemModel;
import dk.belman.gui.components.CustomAppBar;
import dk.belman.gui.interactors.InteractorManager;
import dk.belman.gui.interactors.UserInteractor;
import dk.belman.gui.common.UserModel;
import dk.belman.gui.modals.Modal;
import dk.belman.gui.utils.ModalHandler;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.ResourceBundle;

public class UsersController extends View implements Initializable {
    private final UserInteractor userInteractor = InteractorManager.getInstance().getUserInteractor();
    private final AdminUsersModel model = userInteractor.getAdminUsersModel();

    @FXML
    private HBox hBoxControls;

    @FXML
    private ComboBox<Integer> cmbBoxItemsPerPage;

    @FXML
    private ComboBox<String> cmbBoxFilter;

    @FXML
    private TextField txtFieldSearch;

    @FXML
    private Pagination paginationTbl;

    @FXML
    private TableView<UserModel> tblView;

    @FXML
    private TableColumn<UserModel, String> tblColWorkerId;

    @FXML
    private TableColumn<UserModel, String> tblColName;

    @FXML
    private TableColumn<UserModel, UserRole> tblColRole;

    @FXML
    private TableColumn<UserModel, String> tblColStatus;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableView();
        setupControls();
    }

    @Override
    public void updateAppBar(AppBar appBar) {
        CustomAppBar.updateAppBar(appBar, "Dashboard", false);
    }

    private void setupControls() {
        Button addNewBtn = new Button(null, new FontIcon(Feather.PLUS));
        addNewBtn.getStyleClass().add(Styles.ACCENT);
        addNewBtn.setMaxWidth(50);
        addNewBtn.setOnAction(this::showCreatePopUp);

        hBoxControls.getChildren().add(0, addNewBtn);
    }

    private void showCreatePopUp(ActionEvent actionEvent) {
        ModalHandler.getInstance().getModalOverlay().showFXML(Modal.USER_CREATE_NEW);
    }

    private void setupTableView() {
        tblColWorkerId.setCellValueFactory(cellData -> cellData.getValue().workerIdProperty());

        tblColName.setCellValueFactory(cellData -> {
            String name = cellData.getValue().firstNameProperty().get() + " " + cellData.getValue().lastNameProperty().get();
            return new ReadOnlyStringWrapper(name);
        });

        tblColRole.setCellValueFactory(cellData -> cellData.getValue().roleProperty());

        tblColStatus.setCellValueFactory(cellData -> {
            boolean isActive = cellData.getValue().activeProperty().get();
            String statusText = isActive ? "Active" : "Inactive";
            return new ReadOnlyStringWrapper(statusText);
        });

        tblColStatus.setCellFactory(column -> new TableCell<UserModel, String>() {
            private final Label badge = new Label();
            {
                badge.setTextFill(Color.WHITE);
                badge.setPadding(new Insets(2, 8, 2, 8));
                badge.setStyle("-fx-font-weight: bold;");
            }

            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);

                if (empty || status == null) {
                    setGraphic(null);
                } else {
                    switch (status) {
                        case "Active":
                            badge.setStyle("-fx-background-color: #2ecc71; -fx-background-radius: 4; -fx-text-fill: white");
                            break;
                        case "Inactive":
                            badge.setStyle("-fx-background-color: #e74c3c; -fx-background-radius: 4; -fx-text-fill: white");
                            break;
                        default:
                            badge.setStyle("-fx-background-color: #95a5a6; -fx-background-radius: 4; -fx-text-fill: white");
                            break;
                    }

                    badge.setText(status);
                    setGraphic(badge);
                }
            }
        });

        tblView.setItems(model.observableUsersList());
    }
}
