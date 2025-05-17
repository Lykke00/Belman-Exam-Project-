package dk.belman.enums;

public enum ReportStatus {
    PENDING("Pending"),
    ACCEPTED("Accepted"),
    REJECTED("Rejected");

    private final String status;

    ReportStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public static ReportStatus fromString(String status) {
        for (ReportStatus status1 : values()) {
            if (status1.getStatus().equals(status))
                return status1;
        }
        return null;
    }

    @Override
    public String toString() {
        return status;
    }
}
