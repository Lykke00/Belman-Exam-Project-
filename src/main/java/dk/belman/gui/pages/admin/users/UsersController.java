package dk.belman.gui.pages.admin.users;

import atlantafx.base.theme.Styles;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.mvc.View;
import dk.belman.enums.ReportStatus;
import dk.belman.enums.UserRole;
import dk.belman.gui.common.ReportItemModel;
import dk.belman.gui.components.ContextMenu.MenuItemInfo;
import dk.belman.gui.components.CustomAppBar;
import dk.belman.gui.components.GluonSnackbar;
import dk.belman.gui.interactors.InteractorManager;
import dk.belman.gui.interactors.UserInteractor;
import dk.belman.gui.common.UserModel;
import dk.belman.gui.modals.Modal;
import dk.belman.gui.utils.DialogHandler;
import dk.belman.gui.utils.ModalHandler;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Predicate;

import static dk.belman.gui.components.ContextMenu.CustomContextMenu.createContextMenu;

public class UsersController extends View implements Initializable {
    private final UserInteractor userInteractor = InteractorManager.getInstance().getUserInteractor();
    private final AdminUsersModel model = userInteractor.getAdminUsersModel();

    private final static String ALL_USERS = "All";
    private final static String ACTIVE_USERS = "Active";
    private final static String INACTIVE_USERS = "Inactive";

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

    private String currentFilterValue = ACTIVE_USERS;
    private String currentSearchText = "";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableView();
        setupControls();
        setupContextMenu(tblView);
        setupTablePaginationView();
        setupItemsPerPage();
        setupTextFieldSearch();
        setupComboBoxFilter();
    }

    @Override
    public void updateAppBar(AppBar appBar) {
        CustomAppBar.updateAppBar(appBar, "Dashboard", false);
    }

    private void setupTextFieldSearch() {
        txtFieldSearch.textProperty().addListener((observable, oldValue, searchValue) -> {
            currentSearchText = searchValue;
            applyFilters();
        });
    }

    private void setupComboBoxFilter() {
        ObservableList<String> filterOptions = FXCollections.observableArrayList(ALL_USERS, ACTIVE_USERS, INACTIVE_USERS);
        cmbBoxFilter.setItems(filterOptions);

        cmbBoxFilter.getSelectionModel().select(1);
        currentFilterValue = ACTIVE_USERS;

        cmbBoxFilter.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, selectedValue) -> {
            if (selectedValue != null) {
                currentFilterValue = selectedValue;
                applyFilters();
            }
        });
    }

    private void applyFilters() {
        Predicate<UserModel> combinedFilter = userItem -> {
            boolean matchesFilter = switch (currentFilterValue) {
                case ALL_USERS -> true;
                case ACTIVE_USERS -> userItem.activeProperty().get();
                case INACTIVE_USERS -> !userItem.activeProperty().get();
                default -> false;
            };

            boolean matchesSearch = true;
            if (currentSearchText != null && !currentSearchText.isEmpty()) {
                String lowerCaseFilter = currentSearchText.toLowerCase();
                matchesSearch = userItem.firstNameProperty().get().toLowerCase().contains(lowerCaseFilter)
                        || userItem.lastNameProperty().get().toLowerCase().contains(lowerCaseFilter)
                        || userItem.workerIdProperty().get().toLowerCase().contains(lowerCaseFilter);
            }

            return matchesFilter && matchesSearch;
        };

        model.setFilter(combinedFilter);

        paginationTbl.setCurrentPageIndex(0);
    }

    private void setupItemsPerPage() {
        ObservableList<Integer> itemsPerPageOptions = FXCollections.observableArrayList(10, 25, 50, 100);
        cmbBoxItemsPerPage.setItems(itemsPerPageOptions);
        cmbBoxItemsPerPage.getSelectionModel().select(0);

        cmbBoxItemsPerPage.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                model.pageSizeProperty().set(newValue);
                model.updatePageCount();
                paginationTbl.setPageCount(model.pageCountProperty().get());
                model.setPage(paginationTbl.getCurrentPageIndex(), newValue);
            }
        });
    }

    private void setupContextMenu(TableView<UserModel> tableView) {
        MenuItem editItem = new MenuItem("Edit", new FontIcon(Feather.EYE));
        editItem.setOnAction(e -> loadEditPopUp(tableView.getSelectionModel().getSelectedItem()));

        MenuItem separator = new SeparatorMenuItem();

        MenuItem toggleActiveItem = new MenuItem(); // tom til at starte med
        toggleActiveItem.setOnAction(e -> updateUserStatus(tableView.getSelectionModel().getSelectedItem()));

        ContextMenu contextMenu = new ContextMenu(editItem, separator, toggleActiveItem);

        contextMenu.setOnShowing(e -> {
            UserModel selectedUser = tableView.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                boolean isActive = selectedUser.activeProperty().get();
                toggleActiveItem.setText(isActive ? "Set Inactive" : "Set Active");
                toggleActiveItem.setGraphic(new FontIcon(isActive ? Feather.TRASH : Feather.CHECK));
            }
        });

        tableView.setContextMenu(contextMenu);
    }

    private void updateUserStatus(UserModel user) {
        if (user == null) {
            GluonSnackbar.showSnackbar("No user selected");
            return;
        }

        boolean isActive = user.activeProperty().get();
        if (isActive) {
            loadDialogDeleteUser(user);
        } else {
            loadDialogSetActiveUser(user);
        }
    }

    private void loadDialogSetActiveUser(UserModel user) {
        String message = "Are you sure you want to set user: " + user.firstNameProperty().get() + " " + user.lastNameProperty().get() + " as active?";
        DialogHandler.showConfirmationDialog("Activate", "Confirm activation", message, () -> {
            userInteractor.updateUserStatus(user, true, success -> {
                if (success) {
                    GluonSnackbar.showSnackbar("User activated successfully");
                }
            });
        });
    }

    private void loadDialogDeleteUser(UserModel userModel) {
        String message = "Are you sure you want to set user: " + userModel.firstNameProperty().get() + " " + userModel.lastNameProperty().get() + " as inactive?";
        DialogHandler.showConfirmationDialog("Inactive", "Confirm inactive", message, () -> {
            userInteractor.updateUserStatus(userModel, false, success -> {
                if (success) {
                    GluonSnackbar.showSnackbar("User deleted successfully");
                }
            });
        });
    }

    private void loadEditPopUp(UserModel userModel) {
        InteractorManager.getInstance().getUserInteractor().getUserEditModel().userProperty().set(userModel);
        ModalHandler.getInstance().getModalOverlay().showFXML(Modal.USER_EDIT);
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

    private void setupTablePaginationView() {
        paginationTbl.setCurrentPageIndex(0);
        paginationTbl.setPageCount(1);

        paginationTbl.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> {
            int pageSize = cmbBoxItemsPerPage.getValue() != null ? cmbBoxItemsPerPage.getValue() : 10;
            model.setPage(newIndex.intValue(), pageSize);
        });

        model.pageCountProperty().addListener((obs, oldCount, newCount) -> {
            paginationTbl.setPageCount(newCount.intValue());
        });

        model.databaseLoadingProperty().addListener((obs, wasLoaded, isDatabaseLoading) -> {
            System.out.println(isDatabaseLoading + " " + "nigger");
            if (!isDatabaseLoading) {
                int pageSize = cmbBoxItemsPerPage.getValue() != null ? cmbBoxItemsPerPage.getValue() : 10;
                model.pageSizeProperty().set(pageSize);

                tblView.setItems(model.pagedUsersProperty());

                applyFilters();
                model.updatePageCount();
                paginationTbl.setPageCount(model.pageCountProperty().get());
                model.setPage(0, pageSize);
            }
        });
    }

    private void setupTableView() {
        tblView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tblView.setItems(model.pagedUsersProperty());

        tblView.setPlaceholder(new Label("No users found"));

        tblColWorkerId.setCellValueFactory(cellData -> cellData.getValue().workerIdProperty());

        tblColName.setCellValueFactory(cellData -> {
            UserModel user = cellData.getValue();
            StringBinding fullNameBinding = Bindings.createStringBinding(
                    () -> user.firstNameProperty().get() + " " + user.lastNameProperty().get(),
                    user.firstNameProperty(), user.lastNameProperty()
            );
            return fullNameBinding;
        });

        tblColRole.setCellValueFactory(cellData -> cellData.getValue().roleProperty());

        tblColStatus.setCellValueFactory(cellData -> {
            BooleanProperty active = cellData.getValue().activeProperty();
            StringBinding statusBinding = Bindings.createStringBinding(
                    () -> active.get() ? "Active" : "Inactive",
                    active
            );
            return statusBinding;
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
    }
}
