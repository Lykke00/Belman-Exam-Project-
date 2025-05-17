package dk.belman.gui.pages.inspector.reports;

import dk.belman.gui.pages.common.ReportItemModel;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import java.util.function.Predicate;

public class ReportModel {
    private final ObservableList<ReportItemModel> reports = FXCollections.observableArrayList();
    private final FilteredList<ReportItemModel> filteredReports = new FilteredList<>(reports, s -> true);
    private final FilteredList<ReportItemModel> pagedReports = new FilteredList<>(filteredReports, s -> true);

    private final SimpleBooleanProperty loaded = new SimpleBooleanProperty(false);

    private final IntegerProperty currentPage = new SimpleIntegerProperty(0);
    private final IntegerProperty pageSize = new SimpleIntegerProperty(10);
    private final IntegerProperty pageCount = new SimpleIntegerProperty(1);
    private Predicate<ReportItemModel> userFilter = s -> true;

    public ReportModel() {
        reports.addListener((ListChangeListener<ReportItemModel>) change -> {
            updatePageCount();
            updatePagePredicate();
        });

        filteredReports.addListener((ListChangeListener<ReportItemModel>) change -> {
            updatePageCount();
            updatePagePredicate();
        });
    }

    public ObservableList<ReportItemModel> reportsProperty() {
        return reports;
    }

    public FilteredList<ReportItemModel> filteredReportsProperty() {
        return filteredReports;
    }

    public FilteredList<ReportItemModel> pagedReportsProperty() {
        return pagedReports;
    }

    public void setFilter(Predicate<ReportItemModel> predicate) {
        this.userFilter = predicate;
        filteredReports.setPredicate(predicate);
    }

    public void setPage(int pageIndex, int itemsPerPage) {
        currentPage.set(pageIndex);
        pageSize.set(itemsPerPage);
        updatePagePredicate();
    }

    private void updatePagePredicate() {
        int pageIndex = currentPage.get();
        int itemsPerPage = pageSize.get();

        int fromIndex = pageIndex * itemsPerPage;
        int toIndex = Math.min(fromIndex + itemsPerPage, filteredReports.size());

        pagedReports.setPredicate(report -> {
            int i = filteredReports.indexOf(report);
            return i >= fromIndex && i < toIndex;
        });
    }

    public void updatePageCount() {
        int itemCount = filteredReports.size();
        int newPageCount = (int) Math.ceil((double) itemCount / pageSize.get());
        pageCount.set(Math.max(1, newPageCount));
    }

    public SimpleBooleanProperty loadedProperty() {
        return loaded;
    }

    public IntegerProperty currentPageProperty() {
        return currentPage;
    }

    public IntegerProperty pageSizeProperty() {
        return pageSize;
    }

    public IntegerProperty pageCountProperty() {
        return pageCount;
    }

    public int getCurrentPage() {
        return currentPage.get();
    }

    public int getPageSize() {
        return pageSize.get();
    }

    public int getPageCount() {
        return pageCount.get();
    }
}