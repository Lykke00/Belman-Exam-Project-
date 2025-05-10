package dk.belman.gui.pages.operator.PictureProcess;

import dk.belman.be.ReportImage;
import dk.belman.be.OperatorReport;
import dk.belman.be.User;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;

import static dk.belman.gui.utils.ImageUtilities.convertImageToPngBytes;
import static dk.belman.gui.utils.ImageUtilities.convertImageToPngBytesParallel;

public class PictureProcessModel {
    private final SimpleStringProperty qcReportId = new SimpleStringProperty();

    //hjernen bag det hele og holder styr på ALT
    private final SimpleObjectProperty<CurrentStateProcess> state = new SimpleObjectProperty<>(CurrentStateProcess.BEGIN);

    // vi gemmer alle states i en liste, for at kunne samenligne med vores ovenstående state
    private final ObservableMap<CurrentStateProcess, PictureItemModel> stateList = FXCollections.observableHashMap();

    private final SimpleBooleanProperty databaseLoading = new SimpleBooleanProperty(false);

    public PictureProcessModel() {
        initializeStateList();
    }

    private void initializeStateList() {
        for (CurrentStateProcess state : CurrentStateProcess.values()) {
            if (state.equals(CurrentStateProcess.BEGIN)
                    || state.equals(CurrentStateProcess.FINISH)) // vi har ikke brug for at gemme data på disse sider
                continue;

            stateList.put(state, new PictureItemModel());
        }
    }

    public SimpleStringProperty qcReportIdProperty() {
        return qcReportId;
    }

    public SimpleObjectProperty<CurrentStateProcess> stateProperty() {
        return state;
    }

    public ObservableMap<CurrentStateProcess, PictureItemModel> getStateList() {
        return stateList;
    }

    public SimpleBooleanProperty databaseLoadingProperty() {
        return databaseLoading;
    }

    public static OperatorReport toEntity(PictureProcessModel pictureProcessModel) {
        String qcReportId = pictureProcessModel.qcReportIdProperty().get();
        List<ReportImage> imageEntities = new ArrayList<>();

        for (CurrentStateProcess state : pictureProcessModel.getStateList().keySet()) {
            PictureItemModel page = pictureProcessModel.getStateList().get(state);

            String takenFromAngle = state.textProperty();
            Image image = page.pictureProperty().get();
            String comment = page.commentProperty().get();

            if (image != null) {
                byte[] imageBytes = convertImageToPngBytesParallel(image);
                ReportImage reportImage = new ReportImage(imageBytes, comment, takenFromAngle);
                imageEntities.add(reportImage);
            }
        }

        User user = new User();
        user.setId(1);
        return new OperatorReport(qcReportId, user, imageEntities);
    }
}
