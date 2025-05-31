package dk.belman.gui.pages.admin.users;

import dk.belman.gui.common.UserModel;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

import java.util.Comparator;
import java.util.function.Predicate;

public class AdminUsersModel {
    private final ObservableList<UserModel> users = FXCollections.observableArrayList();
    private final SortedList<UserModel> sortedUsers = new SortedList<>(users);
    private final FilteredList<UserModel> filteredUsers = new FilteredList<>(sortedUsers, s -> true);
    private final FilteredList<UserModel> pagedUsers = new FilteredList<>(filteredUsers, s -> true);

    private Predicate<UserModel> userFilter = s -> true;

    private final SimpleIntegerProperty currentPage = new SimpleIntegerProperty(0);
    private final SimpleIntegerProperty pageSize = new SimpleIntegerProperty(10);
    private final SimpleIntegerProperty pageCount = new SimpleIntegerProperty(1);

    public AdminUsersModel() {
        sortByNewest();

        users.addListener((ListChangeListener<UserModel>) change -> {
            updatePageCount();
            updatePagePredicate();
        });
    }

    private void sortByNewest() {
        sortedUsers.setComparator(Comparator.comparing(
                UserModel::getId,
                Comparator.reverseOrder()
        ));
    }

    public void setFilter(Predicate<UserModel> predicate) {
        this.userFilter = predicate;
        filteredUsers.setPredicate(predicate);

        updatePageCount();
        updatePagePredicate();
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
        int toIndex = Math.min(fromIndex + itemsPerPage, filteredUsers.size());

        pagedUsers.setPredicate(user -> {
            int i = filteredUsers.indexOf(user);
            return i >= fromIndex && i < toIndex;
        });
    }

    public void updatePageCount() {
        int itemCount = filteredUsers.size();
        int newPageCount = (int) Math.ceil((double) itemCount / pageSize.get());
        pageCount.set(Math.max(1, newPageCount));
    }

    public SortedList<UserModel> sortedUsersProperty() {
        return sortedUsers;
    }

    public FilteredList<UserModel> filteredUsersProperty() {
        return filteredUsers;
    }

    public FilteredList<UserModel> pagedUsersProperty() {
        return pagedUsers;
    }

    public SimpleIntegerProperty currentPageProperty() {
        return currentPage;
    }

    public SimpleIntegerProperty pageSizeProperty() {
        return pageSize;
    }

    public SimpleIntegerProperty pageCountProperty() {
        return pageCount;
    }

    private final SimpleBooleanProperty databaseLoading = new SimpleBooleanProperty(false);

    public ObservableList<UserModel> observableUsersList() {
        return users;
    }

    public SimpleBooleanProperty databaseLoadingProperty() {
        return databaseLoading;
    }
}