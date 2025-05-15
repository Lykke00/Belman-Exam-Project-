package dk.belman.gui.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.control.TableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.ObservableList;
import javafx.beans.property.*;

/**
 * A custom TableView component that replicates the modern design
 * shown in the reference image.
 */
public class BigScootsTableView<T extends BigScootsTableView.SiteDataModel> extends TableView<T> {

    /**
     * Creates a new BigScootsTableView instance.
     */
    public BigScootsTableView() {
        getStylesheets().add(ModernTableView.class.getResource("/css/tableview2.css").toExternalForm());

        getStyleClass().add("modern-table-view");
        setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        setupColumns();
    }

    /**
     * Setup the columns for the table view.
     */
    @SuppressWarnings("unchecked")
    private void setupColumns() {
        // Domain & Plan Name column
        TableColumn<T, String> domainColumn = new TableColumn<>("Domain & Plan Name");
        domainColumn.setCellValueFactory(new PropertyValueFactory<>("domain"));
        domainColumn.setCellFactory(column -> new TableCell<T, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    T siteData = getTableView().getItems().get(getIndex());

                    VBox container = new VBox(5);
                    container.setPadding(new Insets(10, 0, 10, 0));
                    container.setAlignment(Pos.CENTER_LEFT);

                    Label domainLabel = new Label(item);
                    domainLabel.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 14));

                    HBox planBox = new HBox(5);
                    planBox.setAlignment(Pos.CENTER_LEFT);

                    Circle planIndicator = new Circle(8);
                    planIndicator.setFill(Color.web("#4169e1")); // Royal blue

                    Label planLabel = new Label(siteData.getPlanName());
                    planLabel.setFont(Font.font("Segoe UI", 12));
                    planLabel.setTextFill(Color.web("#666666"));

                    planBox.getChildren().addAll(planIndicator, planLabel);

                    if (siteData.hasSubdomain()) {
                        Label subdomainLabel = new Label(siteData.getSubdomain());
                        subdomainLabel.setFont(Font.font("Segoe UI", 12));
                        subdomainLabel.setTextFill(Color.web("#666666"));
                        container.getChildren().addAll(domainLabel, planBox, subdomainLabel);
                    } else {
                        container.getChildren().addAll(domainLabel, planBox);
                    }

                    setGraphic(container);
                }
            }
        });

        // Storage column
        TableColumn<T, Double> storageColumn = new TableColumn<>("Storage");
        storageColumn.setCellValueFactory(new PropertyValueFactory<>("storageUsed"));
        storageColumn.setCellFactory(column -> new TableCell<T, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    T siteData = getTableView().getItems().get(getIndex());

                    VBox container = new VBox(5);
                    container.setPadding(new Insets(10, 0, 10, 0));

                    // For domains with storage metrics
                    if (siteData.isDomain()) {
                        HBox progressContainer = new HBox(0);
                        progressContainer.setMaxWidth(120);
                        progressContainer.setMinWidth(120);

                        double percentage = item / siteData.getStorageTotal() * 100;

                        Rectangle usedPart = new Rectangle(percentage * 1.2, 8);
                        usedPart.setArcWidth(4);
                        usedPart.setArcHeight(4);
                        usedPart.setFill(getSiteColor(siteData.getType()));

                        Rectangle remainingPart = new Rectangle((100 - percentage) * 1.2, 8);
                        remainingPart.setArcWidth(4);
                        remainingPart.setArcHeight(4);
                        remainingPart.setFill(Color.web("#e0e0e0"));

                        progressContainer.getChildren().addAll(usedPart, remainingPart);

                        Label usageLabel = new Label(String.format("%.2f GB of %.1f GB", item, siteData.getStorageTotal()));
                        usageLabel.setFont(Font.font("Segoe UI", 12));

                        container.getChildren().addAll(progressContainer, usageLabel);
                    } else if (siteData.getTrafficUsed() > 0) {
                        // For subdomains with traffic metrics
                        Label trafficLabel = new Label(String.format("%.1f M", siteData.getTrafficUsed()));
                        Circle trafficIndicator = new Circle(6);
                        trafficIndicator.setFill(getSiteColor(siteData.getType()));

                        HBox trafficBox = new HBox(5);
                        trafficBox.setAlignment(Pos.CENTER_LEFT);
                        trafficBox.getChildren().addAll(trafficIndicator, trafficLabel);

                        container.getChildren().add(trafficBox);
                    } else {
                        setText("-");
                    }

                    setGraphic(container);
                }
            }
        });

        // Monthly Visitor column
        TableColumn<T, Double> visitorColumn = new TableColumn<>("Monthly Visitor");
        visitorColumn.setCellValueFactory(new PropertyValueFactory<>("monthlyVisitor"));
        visitorColumn.setCellFactory(column -> new TableCell<T, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    T siteData = getTableView().getItems().get(getIndex());

                    // For domains with visitor metrics
                    if (siteData.isDomain()) {
                        VBox container = new VBox(5);
                        container.setPadding(new Insets(10, 0, 10, 0));

                        HBox progressContainer = new HBox(0);
                        progressContainer.setMaxWidth(120);
                        progressContainer.setMinWidth(120);

                        double percentage = item / siteData.getVisitorTotal() * 100;

                        Rectangle usedPart = new Rectangle(percentage * 1.2, 8);
                        usedPart.setArcWidth(4);
                        usedPart.setArcHeight(4);
                        usedPart.setFill(getSiteColor(siteData.getType()));

                        Rectangle remainingPart = new Rectangle((100 - percentage) * 1.2, 8);
                        remainingPart.setArcWidth(4);
                        remainingPart.setArcHeight(4);
                        remainingPart.setFill(Color.web("#e0e0e0"));

                        progressContainer.getChildren().addAll(usedPart, remainingPart);

                        Label usageLabel = new Label(String.format("%.2f GB of %.1f GB", item, siteData.getVisitorTotal()));
                        usageLabel.setFont(Font.font("Segoe UI", 12));

                        container.getChildren().addAll(progressContainer, usageLabel);
                        setGraphic(container);
                    } else if (siteData.getTrafficUsed() > 0) {
                        // For subdomains with traffic metrics
                        Label trafficLabel = new Label(String.format("%.1f M", siteData.getTrafficUsed()));
                        Circle trafficIndicator = new Circle(6);
                        trafficIndicator.setFill(getSiteColor(siteData.getType()));

                        HBox trafficBox = new HBox(5);
                        trafficBox.setAlignment(Pos.CENTER_LEFT);
                        trafficBox.getChildren().addAll(trafficIndicator, trafficLabel);

                        setGraphic(trafficBox);
                    } else {
                        setText("-");
                    }
                }
            }
        });

        // Domains column
        TableColumn<T, String> domainsTypeColumn = new TableColumn<>("Domains");
        domainsTypeColumn.setCellValueFactory(new PropertyValueFactory<>("domainType"));
        domainsTypeColumn.setCellFactory(column -> new TableCell<T, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox container = new HBox();
                    container.setAlignment(Pos.CENTER);

                    Label label = new Label(item);
                    label.setPadding(new Insets(5, 10, 5, 10));
                    label.setFont(Font.font("Segoe UI", 12));

                    if (item.equals("Primary")) {
                        label.setStyle("-fx-background-color: #e6efff; -fx-text-fill: #4169e1; -fx-background-radius: 15;");
                    } else if (item.equals("Staging")) {
                        label.setStyle("-fx-background-color: #ffe6f5; -fx-text-fill: #e91e63; -fx-background-radius: 15;");
                    } else if (item.equals("Add-on")) {
                        label.setStyle("-fx-background-color: #fff6e6; -fx-text-fill: #ff9800; -fx-background-radius: 15;");
                    }

                    container.getChildren().add(label);
                    setGraphic(container);
                }
            }
        });

        // Status column
        TableColumn<T, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusColumn.setCellFactory(column -> new TableCell<T, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox container = new HBox();
                    container.setAlignment(Pos.CENTER);

                    Label label = new Label(item);
                    label.setPadding(new Insets(5, 15, 5, 15));
                    label.setFont(Font.font("Segoe UI", 12));

                    if (item.equals("Active")) {
                        label.setStyle("-fx-background-color: #e6fff9; -fx-text-fill: #00bfa5; -fx-background-radius: 15;");
                    }

                    container.getChildren().add(label);
                    setGraphic(container);
                }
            }
        });

        // Usage column
        TableColumn<T, Double> usageColumn = new TableColumn<>("");
        usageColumn.setPrefWidth(80);
        usageColumn.setCellValueFactory(new PropertyValueFactory<>("usagePercentage"));
        usageColumn.setCellFactory(column -> new TableCell<T, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    if (item > 0) {
                        StackPane circularProgress = new StackPane();
                        circularProgress.setPadding(new Insets(5));

                        // Create circular progress indicator
                        double radius = 18;
                        Circle backgroundCircle = new Circle(radius);
                        backgroundCircle.setFill(Color.TRANSPARENT);
                        backgroundCircle.setStroke(Color.web("#f0f0f0"));
                        backgroundCircle.setStrokeWidth(3);

                        // Create progress arc
                        Arc progressArc = createProgressArc(item, radius);

                        // Create percentage label
                        Label percentLabel = new Label(String.format("%.0f%%", item));
                        percentLabel.setFont(Font.font("Segoe UI", 10));
                        percentLabel.setTextFill(Color.web("#666666"));

                        circularProgress.getChildren().addAll(backgroundCircle, progressArc, percentLabel);
                        setGraphic(circularProgress);
                    }
                }
            }

            private Arc createProgressArc(double percentage, double radius) {
                // Calculate start and length angles for the arc
                double startAngle = 90;
                double length = -percentage * 3.6; // Convert percentage to degrees (negative for clockwise)

                Arc arc = new Arc();
                arc.setCenterX(0);
                arc.setCenterY(0);
                arc.setRadiusX(radius);
                arc.setRadiusY(radius);
                arc.setStartAngle(startAngle);
                arc.setLength(length);
                arc.setType(ArcType.OPEN);
                arc.setFill(Color.TRANSPARENT);
                arc.setStroke(Color.web("#4169e1"));
                arc.setStrokeWidth(3);

                return arc;
            }
        });

        // Add all columns to the table view
        getColumns().addAll(domainColumn, storageColumn, visitorColumn,
                domainsTypeColumn, statusColumn, usageColumn);
    }

    /**
     * Returns a color based on the site type.
     *
     * @param type The site type
     * @return A color associated with the site type
     */
    private Color getSiteColor(String type) {
        switch (type.toLowerCase()) {
            case "primary":
                return Color.web("#4169e1"); // Royal Blue
            case "staging":
                return Color.web("#e91e63"); // Pink
            case "add-on":
                return Color.web("#ff9800"); // Orange
            default:
                return Color.web("#4169e1"); // Default Blue
        }
    }

    /**
     * Data model for site data.
     */
    public static class SiteDataModel {
        private final StringProperty domain;
        private final StringProperty planName;
        private final StringProperty subdomain;
        private final BooleanProperty isDomain;
        private final DoubleProperty storageUsed;
        private final DoubleProperty storageTotal;
        private final DoubleProperty monthlyVisitor;
        private final DoubleProperty visitorTotal;
        private final DoubleProperty usagePercentage;
        private final StringProperty type;
        private final StringProperty domainType;
        private final StringProperty status;
        private final DoubleProperty trafficUsed;

        /**
         * Creates a new SiteDataModel instance.
         *
         * @param domain The domain name
         * @param planName The plan name
         * @param isDomain Whether this entry is a main domain
         * @param storageUsed The used storage
         * @param storageTotal The total storage
         * @param monthlyVisitor The monthly visitor count
         * @param visitorTotal The total visitor count
         * @param usagePercentage The usage percentage
         * @param domainType The domain type (Primary, Staging, Add-on)
         * @param status The status (Active, etc.)
         */
        public SiteDataModel(String domain, String planName, boolean isDomain,
                             double storageUsed, double storageTotal,
                             double monthlyVisitor, double visitorTotal,
                             double usagePercentage, String domainType, String status) {
            this.domain = new SimpleStringProperty(domain);
            this.planName = new SimpleStringProperty(planName);
            this.subdomain = new SimpleStringProperty("");
            this.isDomain = new SimpleBooleanProperty(isDomain);
            this.storageUsed = new SimpleDoubleProperty(storageUsed);
            this.storageTotal = new SimpleDoubleProperty(storageTotal);
            this.monthlyVisitor = new SimpleDoubleProperty(monthlyVisitor);
            this.visitorTotal = new SimpleDoubleProperty(visitorTotal);
            this.usagePercentage = new SimpleDoubleProperty(usagePercentage);
            this.type = new SimpleStringProperty(domainType.toLowerCase());
            this.domainType = new SimpleStringProperty(domainType);
            this.status = new SimpleStringProperty(status);
            this.trafficUsed = new SimpleDoubleProperty(0);
        }

        // Getters
        public String getDomain() { return domain.get(); }
        public String getPlanName() { return planName.get(); }
        public String getSubdomain() { return subdomain.get(); }
        public boolean isDomain() { return isDomain.get(); }
        public double getStorageUsed() { return storageUsed.get(); }
        public double getStorageTotal() { return storageTotal.get(); }
        public double getMonthlyVisitor() { return monthlyVisitor.get(); }
        public double getVisitorTotal() { return visitorTotal.get(); }
        public double getUsagePercentage() { return usagePercentage.get(); }
        public String getType() { return type.get(); }
        public String getDomainType() { return domainType.get(); }
        public String getStatus() { return status.get(); }
        public double getTrafficUsed() { return trafficUsed.get(); }

        // Property getters
        public StringProperty domainProperty() { return domain; }
        public StringProperty planNameProperty() { return planName; }
        public StringProperty subdomainProperty() { return subdomain; }
        public BooleanProperty isDomainProperty() { return isDomain; }
        public DoubleProperty storageUsedProperty() { return storageUsed; }
        public DoubleProperty storageTotalProperty() { return storageTotal; }
        public DoubleProperty monthlyVisitorProperty() { return monthlyVisitor; }
        public DoubleProperty visitorTotalProperty() { return visitorTotal; }
        public DoubleProperty usagePercentageProperty() { return usagePercentage; }
        public StringProperty typeProperty() { return type; }
        public StringProperty domainTypeProperty() { return domainType; }
        public StringProperty statusProperty() { return status; }
        public DoubleProperty trafficUsedProperty() { return trafficUsed; }

        // Setters
        public void setSubdomain(String subdomain) { this.subdomain.set(subdomain); }
        public void setTrafficUsed(double traffic) { this.trafficUsed.set(traffic); }

        // Utility methods
        public boolean hasSubdomain() { return !subdomain.get().isEmpty(); }
    }
}