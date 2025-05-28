package dk.belman.gui.pages.inspector.reports;

import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.mvc.View;
import dk.belman.enums.ReportStatus;
import dk.belman.gui.components.ContextMenu.MenuItemInfo;
import dk.belman.gui.components.CustomAppBar;
import dk.belman.gui.interactors.InteractorManager;
import dk.belman.gui.interactors.ReportInteractor;
import dk.belman.gui.modals.Modal;
import dk.belman.gui.common.ReportItemModel;
import dk.belman.gui.utils.ModalHandler;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.feather.Feather;

import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import static dk.belman.gui.components.ContextMenu.CustomContextMenu.createContextMenu;

public class ReportController extends View implements Initializable {
    private final ReportInteractor reportInteractor = InteractorManager.getInstance().getReportInteractor();
    private final ReportModel model = reportInteractor.getReportModel();

    private final static String ALL_REPORTS = "All";
    private final static String PENDING_REPORTS = "Pending";
    private final static String ACCEPTED_REPORTS = "Accepted";
    private final static String REJECTED_REPORTS = "Rejected";

    @FXML
    private ComboBox<String> cmbBoxFilter;

    @FXML
    private ComboBox<Integer> cmbBoxItemsPerPage;

    @FXML
    private TextField txtFieldSearch;

    @FXML
    private Pagination paginationTbl;

    @FXML
    private TableView<ReportItemModel> tblView;

    @FXML
    private TableColumn<ReportItemModel, String> tblColOrderNumber, tblColStatus, tblColCreated, tblColOperator;

    @FXML
    private VBox vBoxMain;

    private String currentFilterValue = PENDING_REPORTS;
    private String currentSearchText = "";

    public ReportController() {
        reportInteractor.initialize();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableView();
        setupTablePaginationView();
        setupItemsPerPage();
        setupTextFieldSearch();
        setupComboBoxFilter();
        setupContextMenu(tblView);


        Button btnRefresh = new Button("Refresh");
        btnRefresh.setOnAction(e -> {addTestData();});
        vBoxMain.getChildren().add(btnRefresh);
    }

    private void addTestData() {
        ReportStatus[] statuses = {ReportStatus.ACCEPTED, ReportStatus.REJECTED, ReportStatus.PENDING};

        Random random = new Random(System.currentTimeMillis());

        ReportItemModel item = new ReportItemModel();
        item.orderNumberProperty().set("Order #" + System.currentTimeMillis() % 1000);

        ReportStatus randomStatus = statuses[random.nextInt(statuses.length)];
        item.statusProperty().set(randomStatus);

        item.createdDateProperty().set(LocalDateTime.now());
        item.operatorIdProperty().set("Operator #" + random.nextInt(10));

        model.reportsProperty().add(item);
    }


    private void setupContextMenu(TableView<ReportItemModel> tableView) {
        List<MenuItemInfo<ReportItemModel>> menuItemInfos = List.of(
                new MenuItemInfo<>(Feather.EYE, new SimpleStringProperty("Show"), this::loadPopUp)/*,
                new MenuItemInfo<>(true),
                new MenuItemInfo<>(Feather.EDIT_2, new SimpleStringProperty("Change status"), System.out::println),
                new MenuItemInfo<>(Feather.BOOK_OPEN, new SimpleStringProperty("Generate PDF"), System.out::println),
                new MenuItemInfo<>(true),
                new MenuItemInfo<>(Feather.TRASH, new SimpleStringProperty("Delete"), System.out::println)*/
            );

        var contextMenu = createContextMenu(menuItemInfos, tableView);

        tableView.setContextMenu(contextMenu);
    }

    private void contextMenuShow(ReportItemModel item) {
        System.out.println("Context menu shown " + item.orderNumberProperty().get());
    }

    private void setupComboBoxFilter() {
        ObservableList<String> filterOptions = FXCollections.observableArrayList(ALL_REPORTS, PENDING_REPORTS, ACCEPTED_REPORTS, REJECTED_REPORTS);
        cmbBoxFilter.setItems(filterOptions);

        cmbBoxFilter.getSelectionModel().select(1);
        currentFilterValue = PENDING_REPORTS;

        cmbBoxFilter.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, selectedValue) -> {
            if (selectedValue != null) {
                currentFilterValue = selectedValue;
                applyFilters();
            }
        });
    }

    private void setupTextFieldSearch() {
        txtFieldSearch.textProperty().addListener((observable, oldValue, searchValue) -> {
            currentSearchText = searchValue;
            applyFilters();
        });
    }

    private void applyFilters() {
        Predicate<ReportItemModel> combinedFilter = reportItem -> {
            boolean matchesFilter = switch (currentFilterValue) {
                case ALL_REPORTS -> true;
                case PENDING_REPORTS -> reportItem.statusProperty().get().equals(ReportStatus.PENDING);
                case ACCEPTED_REPORTS -> reportItem.statusProperty().get().equals(ReportStatus.ACCEPTED);
                case REJECTED_REPORTS -> reportItem.statusProperty().get().equals(ReportStatus.REJECTED);
                default -> false;
            };

            boolean matchesSearch = true;
            if (currentSearchText != null && !currentSearchText.isEmpty()) {
                String lowerCaseFilter = currentSearchText.toLowerCase();
                matchesSearch = reportItem.orderNumberProperty().get().toLowerCase().contains(lowerCaseFilter);
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
                paginationTbl.setPageCount(model.getPageCount());
                model.setPage(paginationTbl.getCurrentPageIndex(), newValue);
            }
        });
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

        model.loadedProperty().addListener((obs, wasLoaded, isNowLoaded) -> {
            System.out.println(isNowLoaded);
            if (isNowLoaded) {
                int pageSize = cmbBoxItemsPerPage.getValue() != null ? cmbBoxItemsPerPage.getValue() : 10;
                model.pageSizeProperty().set(pageSize);

                tblView.setItems(model.pagedReportsProperty());

                applyFilters();
                model.updatePageCount();
                paginationTbl.setPageCount(model.getPageCount());
                model.setPage(0, pageSize);
            }
        });
    }

    private void setupTableView() {
        tblView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tblView.setItems(model.pagedReportsProperty());

        tblView.setPlaceholder(new Label("No reports found"));

        tblColOrderNumber.setCellValueFactory(cellData -> cellData.getValue().orderNumberProperty());
        tblColStatus.setCellValueFactory(cellData ->
                Bindings.createStringBinding(
                        () -> {
                            ReportStatus status = cellData.getValue().statusProperty().get();
                            return status != null ? status.getStatus() : "";
                        },
                        cellData.getValue().statusProperty()
                )
        );

        tblColStatus.setCellFactory(column -> new TableCell<ReportItemModel, String>() {
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
                        case "Pending":
                            badge.setStyle("-fx-background-color: #3498db; -fx-background-radius: 4; -fx-text-fill: white");
                            break;
                        case "Accepted":
                            badge.setStyle("-fx-background-color: #2ecc71; -fx-background-radius: 4; -fx-text-fill: white");
                            break;
                        case "Rejected":
                            badge.setStyle("-fx-background-color: #e74c3c; -fx-background-radius: 4; -fx-text-fill: white");
                            break;
                        default:
                            badge.setStyle("-fx-background-color: #95a5a6; -fx-background-radius: 4; -fx-text-fill: white");
                    }

                    badge.setText(status);
                    setGraphic(badge);
                }
            }
        });

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        tblColCreated.setCellValueFactory(cellData -> {
            LocalDateTime dateTime = cellData.getValue().getCreatedDate();
            String formatted = (dateTime != null) ? formatter.format(dateTime) : "";
            return new ReadOnlyStringWrapper(formatted);
        });

        tblColOperator.setCellValueFactory(cellData -> cellData.getValue().operatorIdProperty());

        tblView.setRowFactory(e -> {
            TableRow<ReportItemModel> row = new TableRow<>();
            row.itemProperty().addListener((obs, oldItem, newItem) -> {
                if (newItem != null) {
                    row.setOnMouseClicked(event -> {
                        if (event.getClickCount() == 2) {
                            loadPopUp(row.getItem());
                        }
                    });
                }
            });
            return row;
        });
    }

    private void loadPopUp(ReportItemModel item) {
        reportInteractor.loadReport(item);

        ModalHandler.getInstance().getModalOverlay().showFXML(Modal.REPORT_ITEM_VIEW);
    }

    @Override
    public void updateAppBar(AppBar appBar) {
        CustomAppBar.updateAppBar(appBar, "Reports", false);
    }
}