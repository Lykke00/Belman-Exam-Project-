package dk.belman.be;

import java.time.LocalDate;
import java.util.Arrays;

public class Report {

    private int id;
    private String orderNumber;
    private String operator;
    private LocalDate date;
    private byte[] photos;
    private String comment;
    private String status;

    public Report(int id, String orderNumber, String operator, LocalDate date, byte[] photos, String comment, String status) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.operator = operator;
        this.date = date;
        this.photos = photos;
        this.comment = comment;
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public byte[] getPhotos() {
        return photos;
    }

    public void setPhotos(byte[] photos) {
        this.photos = photos;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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
                ", date=" + date +
                ", photos=" + Arrays.toString(photos) +
                ", comment='" + comment + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
