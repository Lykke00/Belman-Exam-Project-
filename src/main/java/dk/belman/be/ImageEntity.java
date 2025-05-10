package dk.belman.be;

public class ImageEntity {
    public byte[] image;
    public String comment;
    public String takenFromAngle;

    public ImageEntity(byte[] image, String comment, String takenFromAngle) {
        this.image = image;
        this.comment = comment;
        this.takenFromAngle = takenFromAngle;
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
