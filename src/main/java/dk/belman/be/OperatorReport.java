package dk.belman.be;

import java.time.LocalDateTime;
import java.util.List;

public class OperatorReport {
    private String orderNumber;
    private User operator;
    private LocalDateTime date;
    private List<ReportImage> photos;

    public OperatorReport() {}

    public OperatorReport(String orderNumber, User operator, LocalDateTime date, List<ReportImage> photos) {
        this.orderNumber = orderNumber;
        this.operator = operator;
        this.date = date;
        this.photos = photos;
    }

    public OperatorReport(String orderNumber, User operator, List<ReportImage> photos) {
        this.orderNumber = orderNumber;
        this.operator = operator;
        this.date = LocalDateTime.now();
        this.photos = photos;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public User getOperator() {
        return operator;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public List<ReportImage> getPhotos() {
        return photos;
    }

    @Override
    public String toString() {
        return "OperatorReport{" +
                "orderNumber='" + orderNumber + '\'' +
                ", operator=" + operator +
                ", date=" + date +
                ", photos=" + photos +
                '}';
    }
}
