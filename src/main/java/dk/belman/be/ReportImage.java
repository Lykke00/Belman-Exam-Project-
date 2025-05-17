package dk.belman.be;

public class ReportImage {
    private int id;
    private byte[] image;
    private String comment;
    private String takenFromAngle;

    public ReportImage(int id, byte[] image, String comment, String takenFromAngle) {
        this.id = id;
        this.image = image;
        this.comment = comment;
        this.takenFromAngle = takenFromAngle;
    }

    public ReportImage(byte[] image, String comment, String takenFromAngle) {
        this.image = image;
        this.comment = comment;
        this.takenFromAngle = takenFromAngle;
    }

    public int getId() {
        return id;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTakenFromAngle() {
        return takenFromAngle;
    }

    public void setTakenFromAngle(String takenFromAngle) {
        this.takenFromAngle = takenFromAngle;
    }

    @Override
    public String toString() {
        return "ImageEntity{" +
                "image=" + image +
                ", comment='" + comment + '\'' +
                ", takenFromAngle='" + takenFromAngle + '\'' +
                '}';
    }
}
