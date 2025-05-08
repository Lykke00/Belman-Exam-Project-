package dk.belman.gui.pages.operator;

import com.gluonhq.charm.glisten.application.AppManager;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.control.Dialog;
import com.gluonhq.charm.glisten.control.TextArea;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import dk.belman.gui.components.DrawerManager;
import dk.belman.gui.utils.PDFPreviewer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class QcPreviewController extends View implements Initializable {

    @FXML
    private Button btnAddComment;

    @FXML
    private VBox vBoxMain;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        btnAddComment();

        File file = new File("/Users/lykkebernberg/Desktop/Belman/src/main/resources/jdbc_tutorial.pdf");
        PDFPreviewer pdfPreviewer = new PDFPreviewer(file);
        pdfPreviewer.setFitToWidth(false);
        pdfPreviewer.setMinWidth(700);
        pdfPreviewer.setPrefWidth(700);
        pdfPreviewer.setMaxWidth(700);

        vBoxMain.getChildren().add(0, pdfPreviewer);
    }

    @Override
    public void updateAppBar(AppBar appBar) {
        AppManager appManager = AppManager.getInstance();
        appManager.getAppBar().setTitleText("QC Preview");

        Label id = new Label("#123-456");
        id.setPadding(new Insets(0, 20, 0, 0));
        id.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");

        appBar.getActionItems().add(id);
        appManager.getAppBar().setNavIcon(MaterialDesignIcon.MENU.button(e -> DrawerManager.getInstance().getDrawer().open()));
    }

    private void btnAddComment() {
        btnAddComment.setOnAction(e -> {
            Dialog<String> dialog = new Dialog<>("Add comment");
            dialog.setTitleText("Add comment");
            dialog.getTitle().setStyle("-fx-font-size: 24px; -fx-text-fill: #333; -fx-font-weight: bold;");

            TextArea message = new TextArea();
            message.setPromptText("Enter your comment here...");
            message.setPrefHeight(200);
            message.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #ccc; -fx-border-radius: 8px;");
            message.setPadding(new Insets(5));

            Button closeBtn = new Button("Close");
            closeBtn.setOnAction(ev -> dialog.hide());
            closeBtn.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white;-fx-background-radius: 8px; -fx-font-size: 24");

            Button saveBtn = new Button("Save");
            saveBtn.setOnAction(ev -> dialog.hide());
            saveBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;-fx-background-radius: 8px; -fx-font-size: 24");

            HBox spacer = new HBox();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            HBox hBox = new HBox(closeBtn, spacer, saveBtn);

            VBox content = new VBox(10, message, hBox);
            content.setStyle("-fx-padding: 5; -fx-background-radius: 8px; -fx-border-radius: 8px;");
            dialog.setContent(content);

            dialog.showAndWait();
        });

    }
}



