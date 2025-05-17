package dk.belman.gui.pages.inspector.reportview;

import dk.belman.gui.pages.common.ReportItemModel;
import javafx.beans.property.SimpleBooleanProperty;

public class ReportItemViewModel {
    private ReportItemModel reportItemModelProperty;
    private final SimpleBooleanProperty loadedProperty = new SimpleBooleanProperty(false);

    public ReportItemModel reportItemModelProperty() {
        return reportItemModelProperty;
    }

    public void reportItemModelProperty(ReportItemModel reportItemModel) {
        this.reportItemModelProperty = reportItemModel;
    }

    public SimpleBooleanProperty loadedProperty() {
        return loadedProperty;
    }
}
