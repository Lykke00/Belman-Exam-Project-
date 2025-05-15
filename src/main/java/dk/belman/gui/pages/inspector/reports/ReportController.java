package dk.belman.gui.pages.inspector.reports;

import com.gluonhq.charm.glisten.mvc.View;
import dk.belman.gui.interactors.InteractorManager;
import dk.belman.gui.interactors.ReportInteractor;
import dk.belman.gui.pages.common.ReportItemModel;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.bouncycastle.tsp.TSPUtil;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class ReportController extends View implements Initializable {
    private final ReportInteractor reportInteractor = InteractorManager.getInstance().getReportInteractor();
    private final ReportModel model = reportInteractor.getReportModel();

    @FXML
    private ScrollPane scrollPaneView;

    @FXML
    private VBox vBoxMain;

    private ObservableList<ReportItemModel> siteDetailsList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TableView<ReportItemModel> tableView = atlantaFxTable();

        Pagination pg = new Pagination();
        pg.setPageCount(5);
        pg.setPageFactory(index -> {
            int pageSize = 10;
            int fromIndex = index * pageSize;
            int toIndex = Math.min(fromIndex + pageSize, model.reportsProperty().size());
            tableView.setItems(FXCollections.observableArrayList(model.reportsProperty().subList(fromIndex, toIndex)));
            return tableView;
        });

        vBoxMain.getChildren().addAll(tableView, pg);
    }

    private TableView<ReportItemModel> atlantaFxTable() {
        TableView<ReportItemModel> tableView = new TableView<>();

        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setItems(model.reportsProperty());

        TableColumn<ReportItemModel, String> orderNumberColumn = new TableColumn("Order number");
        orderNumberColumn.setCellValueFactory(cellData -> cellData.getValue().orderNumberProperty());


        TableColumn<ReportItemModel, String> statusColumn = new TableColumn("Status");
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());

        TableColumn<ReportItemModel, String> createdColumn = new TableColumn("Created");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        createdColumn.setCellValueFactory(cellData -> {
            LocalDateTime dateTime = cellData.getValue().getCreatedDate();
            String formatted = (dateTime != null) ? formatter.format(dateTime) : "";
            return new ReadOnlyStringWrapper(formatted);
        });

        TableColumn<ReportItemModel, String> operatorColumn = new TableColumn("Operator");
        operatorColumn.setCellValueFactory(cellData -> cellData.getValue().operatorIdProperty());

        tableView.getColumns().addAll(orderNumberColumn, statusColumn, createdColumn, operatorColumn);

        tableView.setRowFactory(e -> {
            TableRow<ReportItemModel> row = new TableRow<>();
            row.itemProperty().addListener((obs, oldItem, newItem) -> {
                if (newItem != null) {
                    row.setOnMouseClicked(event -> {
                        if (event.getClickCount() == 2) {
                            reportInteractor.loadReport(newItem);
                        }
                    });
                }
            });
            return row;
        });

        return tableView;
    }
}



