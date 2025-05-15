package dk.belman.gui.pages.inspector.reportitemview;

import dk.belman.gui.pages.common.ReportItemModel;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

public class ReportItemViewModel {
    private final SimpleObjectProperty<ReportItemModel> reportItemModelProperty = new SimpleObjectProperty<>();
    private final SimpleBooleanProperty loadedProperty = new SimpleBooleanProperty(false);

    public SimpleObjectProperty<ReportItemModel> reportItemModelProperty() {
        return reportItemModelProperty;
    }

    public SimpleBooleanProperty loadedProperty() {
        return loadedProperty;
    }
}
