package dk.belman.gui.components;

import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.util.Callback;

public class CustomRowFactory<T> implements Callback<TableView<T>, TableRow<T>> {

    @Override
    public TableRow<T> call(TableView<T> tableView) {
        TableRow<T> row = new TableRow<T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setStyle("-fx-background-color: transparent;");
                } else {
                    // Apply rounded corners and shadow to each row
                    setStyle(
                            "-fx-background-color: white;" +
                                    "-fx-background-radius: 12;" +
                                    "-fx-border-radius: 12;" +
                                    "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.03), 4, 0, 0, 1);" +
                                    "-fx-padding: 5;" +
                                    "-fx-background-insets: 0 0 8 0;"
                    );
                }
            }
        };

        // Add hover effect
        row.setOnMouseEntered(e -> {
            if (!row.isEmpty()) {
                row.setStyle(
                        "-fx-background-color: #f7fafc;" +
                                "-fx-background-radius: 12;" +
                                "-fx-border-radius: 12;" +
                                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.06), 6, 0, 0, 2);" +
                                "-fx-padding: 5;" +
                                "-fx-background-insets: 0 0 8 0;"
                );
            }
        });

        row.setOnMouseExited(e -> {
            if (!row.isEmpty()) {
                row.setStyle(
                        "-fx-background-color: white;" +
                                "-fx-background-radius: 12;" +
                                "-fx-border-radius: 12;" +
                                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.03), 4, 0, 0, 1);" +
                                "-fx-padding: 5;" +
                                "-fx-background-insets: 0 0 8 0;"
                );
            }
        });

        return row;
    }
}
