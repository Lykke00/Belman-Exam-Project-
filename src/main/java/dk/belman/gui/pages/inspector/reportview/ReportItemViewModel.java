package dk.belman.gui.pages.inspector.reportview;

import dk.belman.gui.pages.common.ReportItemModel;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

public class ReportItemViewModel {
    private final SimpleObjectProperty<ReportItemModel> reportItemModelProperty = new SimpleObjectProperty<>();
    private final SimpleBooleanProperty loadedProperty = new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty updatingProperty = new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty inspectorUpdatingProperty = new SimpleBooleanProperty(false);

    public SimpleObjectProperty<ReportItemModel> reportItemModelProperty() {
        return reportItemModelProperty;
    }

    public void setReportItemModel(ReportItemModel reportItemModel) {
        this.reportItemModelProperty.set(reportItemModel);
    }

    public SimpleBooleanProperty loadedProperty() {
        return loadedProperty;
    }

    public SimpleBooleanProperty updatingProperty() {
        return updatingProperty;
    }

    public SimpleBooleanProperty inspectorUpdatingProperty() {
        return inspectorUpdatingProperty;
    }
}