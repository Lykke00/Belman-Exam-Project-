package dk.belman.gui.common;

import dk.belman.be.Report;
import dk.belman.enums.ReportStatus;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDateTime;
import java.util.List;

public class ReportItemModel {
    private final SimpleIntegerProperty id = new SimpleIntegerProperty();
    private final SimpleStringProperty orderNumber = new SimpleStringProperty();
    private final SimpleObjectProperty<ReportStatus> status = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<LocalDateTime> createdDate = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<LocalDateTime> updatedDate = new SimpleObjectProperty<>();
    private final SimpleStringProperty operatorId = new SimpleStringProperty();
    private final SimpleStringProperty inspectedBy = new SimpleStringProperty();
    private final SimpleStringProperty inspectorComment = new SimpleStringProperty();

    private final ObservableList<PictureItemModel> images = FXCollections.observableArrayList();

    public IntegerProperty idProperty() {
        return id;
    }

    public SimpleStringProperty orderNumberProperty() {
        return orderNumber;
    }

    public SimpleObjectProperty<ReportStatus> statusProperty() {
        return status;
    }

    public SimpleObjectProperty<LocalDateTime> createdDateProperty() {
        return createdDate;
    }

    public SimpleObjectProperty<LocalDateTime> updatedDateProperty() {
        return updatedDate;
    }

    public SimpleStringProperty operatorIdProperty() {
        return operatorId;
    }

    public SimpleStringProperty inspectedByProperty() {
        return inspectedBy;
    }

    public int getId() {
        return id.get();
    }

    public String getOrderNumber() {
        return orderNumber.get();
    }

    public String getStatus() {
        return status.get().getStatus();
    }

    public LocalDateTime getCreatedDate() {
        return createdDate.get();
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate.get();
    }

    public String getOperatorId() {
        return operatorId.get();
    }

    public String getInspectedBy() {
        return inspectedBy.get();
    }

    public ObservableList<PictureItemModel> getImages() {
        return images;
    }

    public SimpleStringProperty inspectorCommentProperty() {
        return inspectorComment;
    }

    public void setImages(ObservableList<PictureItemModel> images) {
        this.images.clear();
        this.images.addAll(images);
    }

    public void copyFromEntity(Report report) {
        this.id.set(report.getId());
        this.orderNumber.set(report.getOrderNumber());
        this.status.set(report.getStatus());
        this.createdDate.set(report.getCreatedDate());
        this.updatedDate.set(report.getUpdatedDate());
        this.operatorId.set(report.getOperator());
        this.inspectedBy.set("inspector");
        this.inspectorComment.set(report.getInspectorComment());

        if (report.getPhotos() != null) {
            List<PictureItemModel> pictures = report.getPhotos().stream()
                    .map(PictureItemModel::fromEntity)
                    .toList();

            this.images.setAll(pictures);
        }
    }

    public static ReportItemModel fromEntity(Report report) {
        ReportItemModel model = new ReportItemModel();
        model.id.set(report.getId());
        model.orderNumber.set(report.getOrderNumber());
        model.status.set(report.getStatus());
        model.createdDate.set(report.getCreatedDate());
        model.updatedDate.set(report.getUpdatedDate());
        model.operatorId.set(report.getOperator());
        model.inspectedBy.set("inspector");
        model.inspectorCommentProperty().set(report.getInspectorComment());

        if (report.getPhotos() != null) {
            List<PictureItemModel> pictures = report.getPhotos().stream()
                    .map(PictureItemModel::fromEntity)
                    .toList();

            model.images.setAll(pictures);
        }

        return model;
    }

    public static Report toEntity(ReportItemModel model) {
        Report report = new Report();
        report.setId(model.idProperty().get());
        report.setOrderNumber(model.orderNumberProperty().get());
        report.setStatus(model.statusProperty().get());
        report.setCreatedDate(model.createdDateProperty().get());
        report.setUpdatedDate(model.updatedDateProperty().get());
        report.setOperator(model.operatorIdProperty().get());
        report.setInspectorComment(model.inspectedByProperty().get());
        return report;
    }
}
