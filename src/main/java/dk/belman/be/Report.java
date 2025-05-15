package dk.belman.be;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class Report {

    private int id;
    private String orderNumber;
    private String operator;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private List<byte[]> photos;
    private String status;

    public Report () {}

    public Report(int id, String orderNumber, String operator, LocalDateTime createdDate, LocalDateTime updatedDate, List<byte[]> photos, String status) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.operator = operator;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.photos = photos;
        this.status = status;
    }

    public Report(int id, String orderNumber, String operator, LocalDateTime createdDate, LocalDateTime updatedDate, String status) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.operator = operator;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.status = status;
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

    public List<byte[]> getPhotos() {
        return photos;
    }

    public void setPhotos(List<byte[]> photos) {
        this.photos = photos;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
