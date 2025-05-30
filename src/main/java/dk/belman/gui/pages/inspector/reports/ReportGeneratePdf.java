package dk.belman.gui.pages.inspector.reports;

import dk.belman.gui.common.ReportItemModel;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

public class ReportGeneratePdf {
    private final SimpleObjectProperty<ReportItemModel> reportModel = new SimpleObjectProperty<>();

    private final SimpleBooleanProperty databaseLoading = new SimpleBooleanProperty(false);

    public SimpleObjectProperty<ReportItemModel> reportModelProperty() {
        return reportModel;
    }

    public SimpleBooleanProperty databaseLoadingProperty() {
        return databaseLoading;
    }
}
