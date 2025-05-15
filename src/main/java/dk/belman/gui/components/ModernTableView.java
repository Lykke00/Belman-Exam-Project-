package dk.belman.gui.components;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.Objects;

public class ModernTableView<T> extends VBox {

    private final TableView<T> tableView;
    private final ObjectProperty<ObservableList<T>> items = new SimpleObjectProperty<>(FXCollections.observableArrayList());
    private final HBox header;
    private final HBox tableControls;
    private final HBox pagination;
    private final Label titleLabel;
    private final Button addButton;
    private final ComboBox<String> entriesCombo;
    private final TextField searchField;
    private final StackPane tableContainer;

    public ModernTableView() {
        super(20);
        setPadding(new Insets(20));
        setStyle("-fx-background-color: #f5f7fa;");

        // Create components
        this.header = createHeader();
        this.tableControls = createTableControls();

        // Create TableView with custom styling
        this.tableView = new TableView<>();
        this.tableView.getStylesheets().add(ModernTableView.class.getResource("/css/tableview.css").toExternalForm());


        this.tableView.getStyleClass().add("modern-table-view");
        this.tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        this.tableView.setRowFactory(new CustomRowFactory<>());

        // Create a container for the TableView with rounded corners
        this.tableContainer = new StackPane();
        this.tableContainer.getStyleClass().add("table-container");
        this.tableContainer.getChildren().add(tableView);

        this.pagination = createPagination();

        // Extract key components for public access
        this.titleLabel = (Label) header.getChildren().get(0);
        this.addButton = (Button) header.getChildren().get(2);
        this.entriesCombo = (ComboBox<String>) tableControls.getChildren().get(1);
        this.searchField = (TextField) tableControls.getChildren().get(4);

        // Bind items
        this.tableView.itemsProperty().bind(items);

        // Add all components to the layout
        getChildren().addAll(header, tableControls, tableContainer, pagination);
    }

    private HBox createHeader() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(20);

        Label titleLabel = new Label("Site Details");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 24));
        titleLabel.setStyle("-fx-text-fill: #2d3748;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addButton = new Button("+ Add New Site");
        addButton.getStyleClass().add("add-button");

        header.getChildren().addAll(titleLabel, spacer, addButton);
        return header;
    }

    private HBox createTableControls() {
        HBox controls = new HBox(15);
        controls.setAlignment(Pos.CENTER_LEFT);

        Label showLabel = new Label("Show");
        showLabel.setStyle("-fx-text-fill: #4a5568;");

        ComboBox<String> entriesCombo = new ComboBox<>();
        entriesCombo.getItems().addAll("10", "25", "50", "100");
        entriesCombo.setValue("10");
        entriesCombo.setPrefWidth(80);
        entriesCombo.getStyleClass().add("entries-combo");

        Label entriesLabel = new Label("Entries");
        entriesLabel.setStyle("-fx-text-fill: #4a5568;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        TextField searchField = new TextField();
        searchField.setPromptText("Search records");
        searchField.setPrefWidth(200);
        searchField.getStyleClass().add("search-field");

        controls.getChildren().addAll(showLabel, entriesCombo, entriesLabel, spacer, searchField);
        return controls;
    }

    private HBox createPagination() {
        HBox pagination = new HBox(10);
        pagination.setAlignment(Pos.CENTER_LEFT);

        Label infoLabel = new Label("Show 1 to 3 of 3 entries");
        infoLabel.setStyle("-fx-text-fill: #718096;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox pageControls = new HBox(5);
        pageControls.setAlignment(Pos.CENTER);

        Button prevButton = new Button("<");
        prevButton.getStyleClass().add("page-button");

        Button page1 = new Button("1");
        page1.getStyleClass().addAll("page-button", "active-page");

        Button page2 = new Button("2");
        page2.getStyleClass().add("page-button");

        Button page3 = new Button("3");
        page3.getStyleClass().add("page-button");

        Button nextButton = new Button(">");
        nextButton.getStyleClass().add("page-button");

        pageControls.getChildren().addAll(prevButton, page1, page2, page3, nextButton);
        pagination.getChildren().addAll(infoLabel, spacer, pageControls);

        return pagination;
    }

    // Public API

    public TableView<T> getTableView() {
        return tableView;
    }

    public StackPane getTableContainer() {
        return tableContainer;
    }

    public void setTitle(String title) {
        titleLabel.setText(title);
    }

    public void setAddButtonText(String text) {
        addButton.setText(text);
    }

    public void setAddButtonAction(Runnable action) {
        addButton.setOnAction(e -> action.run());
    }

    public void setItems(ObservableList<T> items) {
        this.items.set(items);
    }

    public ObservableList<T> getItems() {
        return items.get();
    }

    public ObjectProperty<ObservableList<T>> itemsProperty() {
        return items;
    }

    public void addColumn(TableColumn<T, ?> column) {
        tableView.getColumns().add(column);
    }

    public ComboBox<String> getEntriesCombo() {
        return entriesCombo;
    }

    public TextField getSearchField() {
        return searchField;
    }

    public Button getAddButton() {
        return addButton;
    }

    // Helper methods for creating common cell factories

    public static <S> TableCell<S, String> createDomainCell() {
        return new TableCell<S, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    VBox container = new VBox(5);
                    container.setPadding(new Insets(8, 0, 8, 0));

                    Label domainLabel = new Label(item);
                    domainLabel.setFont(Font.font("System", FontWeight.MEDIUM, 14));
                    domainLabel.setTextFill(Color.web("#2d3748"));

                    HBox planBox = new HBox(5);
                    planBox.setAlignment(Pos.CENTER_LEFT);

                    Circle circle = new Circle(6);
                    circle.setFill(Color.web("#4299e1"));

                    Label planLabel = new Label("Professional Plan");
                    planLabel.setStyle("-fx-text-fill: #718096; -fx-font-size: 12px;");

                    planBox.getChildren().addAll(circle, planLabel);
                    container.getChildren().addAll(domainLabel, planBox);

                    setGraphic(container);
                    setText(null);
                }
            }
        };
    }

    public static <S> TableCell<S, Double> createStorageCell() {
        return new TableCell<S, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    VBox container = new VBox(5);
                    container.setPadding(new Insets(8, 0, 8, 0));

                    // Create a custom progress bar with gradient
                    ProgressBar progressBar = new ProgressBar(item);
                    progressBar.setPrefWidth(150);
                    progressBar.setPrefHeight(8);
                    progressBar.getStyleClass().add("storage-progress");

                    // Set different colors based on the value
                    if (item < 0.5) {
                        progressBar.setStyle("-fx-accent: linear-gradient(to right, #4299e1, #63b3ed);");
                    } else if (item < 0.8) {
                        progressBar.setStyle("-fx-accent: linear-gradient(to right, #48bb78, #68d391);");
                    } else {
                        progressBar.setStyle("-fx-accent: linear-gradient(to right, #ed8936, #f6ad55);");
                    }

                    Label storageLabel = new Label("35.36 GB of 1.2 GB");
                    storageLabel.setStyle("-fx-text-fill: #718096; -fx-font-size: 12px;");

                    container.getChildren().addAll(progressBar, storageLabel);
                    setGraphic(container);
                    setText(null);
                }
            }
        };
    }

    public static <S> TableCell<S, String> createVisitorCell() {
        return new TableCell<S, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    HBox container = new HBox(8);
                    container.setAlignment(Pos.CENTER_LEFT);
                    container.setPadding(new Insets(8, 0, 8, 0));

                    Label visitorLabel = new Label(item);
                    visitorLabel.setFont(Font.font("System", 14));
                    visitorLabel.setTextFill(Color.web("#2d3748"));

                    if (!item.equals("-")) {
                        Circle indicator = new Circle(4);
                        indicator.setFill(Color.web("#48bb78"));
                        container.getChildren().addAll(visitorLabel, indicator);
                    } else {
                        container.getChildren().add(visitorLabel);
                    }

                    setGraphic(container);
                    setText(null);
                }
            }
        };
    }

    public static <S> TableCell<S, String> createDomainTypeCell() {
        return new TableCell<S, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    HBox container = new HBox();
                    container.setAlignment(Pos.CENTER_LEFT);
                    container.setPadding(new Insets(8, 0, 8, 0));

                    Label badge = new Label(item);
                    badge.getStyleClass().add("domain-badge");

                    // Apply different styles based on domain type
                    if (item.equals("Primary")) {
                        badge.getStyleClass().add("primary-domain");
                    } else if (item.equals("Staging")) {
                        badge.getStyleClass().add("staging-domain");
                    } else if (item.equals("Add-on")) {
                        badge.getStyleClass().add("addon-domain");
                    }

                    container.getChildren().add(badge);
                    setGraphic(container);
                    setText(null);
                }
            }
        };
    }

    public static <S> TableCell<S, String> createStatusCell() {
        return new TableCell<S, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    HBox container = new HBox();
                    container.setAlignment(Pos.CENTER_LEFT);
                    container.setPadding(new Insets(8, 0, 8, 0));

                    Label statusBadge = new Label(item);
                    statusBadge.getStyleClass().addAll("status-badge", "active-status");

                    container.getChildren().add(statusBadge);
                    setGraphic(container);
                    setText(null);
                }
            }
        };
    }
}