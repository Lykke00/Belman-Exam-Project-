package dk.belman.gui.modals.sendemail;

import dk.belman.gui.common.ReportItemModel;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

public class SendEmailModel {
    private final SimpleObjectProperty<ReportItemModel> reportItemModel = new SimpleObjectProperty<>();

    private final SimpleBooleanProperty databaseLoading = new SimpleBooleanProperty(false);

    public final ReportItemModel getReportItemModel() {
        return reportItemModel.get();
    }

    public final void setReportItemModel(ReportItemModel reportItemModel) {
        this.reportItemModel.set(reportItemModel);
    }

    public final SimpleObjectProperty<ReportItemModel> reportItemModelProperty() {
        return reportItemModel;
    }

    public final SimpleBooleanProperty databaseLoadingProperty() {
        return databaseLoading;
    }
}
