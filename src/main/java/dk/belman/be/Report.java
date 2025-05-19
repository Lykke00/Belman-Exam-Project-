package dk.belman.be;

import dk.belman.enums.ReportStatus;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class Report {

    private int id;
    private String orderNumber;
    private String operator;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private List<ReportImage> photos;
    private ReportStatus status;
    private String inspectorComment;

    public Report () {}

    public Report(int id, String orderNumber, String operator, LocalDateTime createdDate, LocalDateTime updatedDate, List<ReportImage> photos, String status, String inspectorComment) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.operator = operator;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.photos = photos;
        this.status = ReportStatus.fromString(status);
        this.inspectorComment = inspectorComment;
    }

    public Report(int id, String orderNumber, String operator, LocalDateTime createdDate, LocalDateTime updatedDate, String status, String inspectorComment) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.operator = operator;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.status = ReportStatus.fromString(status);
        this.inspectorComment = inspectorComment;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    public List<ReportImage> getPhotos() {
        return photos;
    }

    public void setPhotos(List<ReportImage> photos) {
        this.photos = photos;
    }

    public ReportStatus getStatus() {
        return status;
    }

    public void setStatus(ReportStatus status) {
        this.status = status;
    }

    public String getInspectorComment() {
        return inspectorComment;
    }

    public void setInspectorComment(String inspectorComment) {
        this.inspectorComment = inspectorComment;
    }

    @Override
    public String toString() {
        return "Report{" +
                "id=" + id +
                ", orderNumber='" + orderNumber + '\'' +
                ", operator='" + operator + '\'' +
                ", createdDate=" + createdDate +
                ", updatedDate=" + updatedDate +
                ", photos=" + Arrays.toString(photos.toArray()) +
                ", status='" + status + '\'' +
                '}';
    }
}
