package dk.belman.gui.components;

import com.gluonhq.charm.glisten.control.Dialog;
import com.gluonhq.charm.glisten.control.TextArea;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import dk.belman.gui.utils.IconStyle;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class OperatorPicture extends StackPane {
    private boolean isSelected = false;
    private Image image;
    private ImageView imageView;
    private PauseTransition longPressPause;
    private SimpleStringProperty comment = new SimpleStringProperty("");
    private Button retakeBtn;
    private EventHandler<ActionEvent> retakeAction;
    private String position;
    private Rectangle rectangle;

    public OperatorPicture(String imagePath, String pos, int width, int height) {
        this.image = new Image(imagePath);
        this.imageView = new ImageView(image);
        this.position = pos;
        this.initialize(image, pos, width, height);
    }

    public OperatorPicture(Image image, String pos, int width, int height) {
        this.image = image;
        this.imageView = new ImageView(image);
        this.position = pos;
        this.initialize(image, pos, width, height);
    }

    private void initialize(Image image, String pos, int width, int height) {
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);

        ImagePattern pattern = new ImagePattern(image);

        rectangle = new Rectangle(0, 0, width, height);
        rectangle.setArcWidth(10);
        rectangle.setArcHeight(10);

        rectangle.setFill(pattern);

        longPressPause = new PauseTransition(Duration.seconds(0.5));
        longPressPause.setOnFinished(event -> showImageDialog());

        rectangle.setOnMousePressed(this::handleMousePressed);
        rectangle.setOnMouseReleased(this::handleMouseReleased);

        VBox pictureDetails = new VBox();
        pictureDetails.setAlignment(Pos.TOP_LEFT);
        pictureDetails.setPadding(new Insets(5));
        pictureDetails.setSpacing(5);
        VBox.setVgrow(pictureDetails, Priority.ALWAYS);

        Label picturePosition = new Label(pos);
        picturePosition.setStyle("-fx-text-fill: black; -fx-font-size: 18px; -fx-font-weight: bold;");

        Button addCommentBtn = new Button("Comment");
        addCommentBtn.getStyleClass().add("secondary-color");
        addCommentBtn.setStyle("-fx-text-fill: white; -fx-background-radius: 8px; -fx-font-size: 16");
        addCommentBtn.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        HBox.setHgrow(addCommentBtn, Priority.ALWAYS);

        addCommentBtn.setOnAction(this::btnAddComment);

        retakeBtn = new Button("Retake");
        retakeBtn.getStyleClass().add("warning-color");
        retakeBtn.setStyle("-fx-text-fill: white; -fx-background-radius: 8px; -fx-font-size: 16");
        retakeBtn.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        HBox.setHgrow(retakeBtn, Priority.ALWAYS);

        retakeBtn.setOnAction(this::handleRetakeAction);

        addCommentBtn.setFocusTraversable(false);
        retakeBtn.setFocusTraversable(false);

        VBox spacer = new VBox();
        spacer.setMinHeight(0);
        VBox.setVgrow(spacer, Priority.ALWAYS);

        pictureDetails.getChildren().addAll(picturePosition, spacer, addCommentBtn, retakeBtn);

        VBox vbox = new VBox();
        vbox.setAlignment(Pos.TOP_CENTER);
        vbox.getChildren().addAll(rectangle, pictureDetails);

        this.setAlignment(Pos.TOP_CENTER);
        this.getChildren().add(vbox);

        this.setPadding(new Insets(10, 10, 10, 10));

        this.getStylesheets().add(getClass().getResource("/css/belman.css").toExternalForm());
        this.getStyleClass().add("picture-process-preview-container");
        this.setPickOnBounds(true);
    }

    private void handleRetakeAction(ActionEvent event) {
        if (retakeAction != null) {
            retakeAction.handle(event);
        } else {
            System.out.println("Retake clicked for position: " + position);
        }
    }

    private void handleMousePressed(MouseEvent event) {
        longPressPause.play();
    }

    private void handleMouseReleased(MouseEvent event) {
        longPressPause.stop();
    }

    private void showImageDialog() {
        ResizableDialog<HBox> dialog = new ResizableDialog<>("Image Preview");

        Image image = imageView.getImage();
        ImageView dialogImageView = new ImageView(image);
        dialogImageView.setPreserveRatio(true);

        double maxWidth = 800;
        double maxHeight = 600;

        double imageWidth = image.getWidth();
        double imageHeight = image.getHeight();

        double scale = Math.min(
                maxWidth / imageWidth,
                maxHeight / imageHeight
        );

        if (scale < 1) {
            dialogImageView.setFitWidth(imageWidth * scale);
            dialogImageView.setFitHeight(imageHeight * scale);
        } else {
            dialogImageView.setFitWidth(imageWidth);
            dialogImageView.setFitHeight(imageHeight);
        }

        dialogImageView.setOnMouseClicked(event -> {
            dialog.hide();
        });

        dialog.setDialogSize(0, 0);

        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);

        hBox.getChildren().add(dialogImageView);
        dialog.setContent(hBox);

        dialog.showAndWait();
    }

    private void btnAddComment(ActionEvent event) {
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
        saveBtn.setOnAction(ev -> {
            dialog.hide();
            GluonSnackbar.showSnackbar("Comment saved: " + comment.get());
        });

        comment.bind(message.textProperty());

        saveBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;-fx-background-radius: 8px; -fx-font-size: 24");

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox hBox = new HBox(closeBtn, spacer, saveBtn);

        VBox content = new VBox(10, message, hBox);
        content.setStyle("-fx-padding: 5; -fx-background-radius: 8px; -fx-border-radius: 8px;");
        dialog.setContent(content);

        dialog.showAndWait();
    }

    // Public methods to access and configure the retake functionality

    /**
     * Set a custom action for the retake button
     * @param action The action to perform when retake is clicked
     */
    public void setRetakeAction(EventHandler<ActionEvent> action) {
        this.retakeAction = action;
    }

    /**
     * Get the retake button for direct access (if needed)
     * @return The retake button
     */
    public Button getRetakeButton() {
        return retakeBtn;
    }

    /**
     * Get the position string for identification
     * @return The position string
     */
    public String getPosition() {
        return position;
    }

    public SimpleStringProperty getComment() {
        return comment;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public Image getImage() {
        return imageView.getImage();
    }

    public void updateImage(Image newImage) {
        this.image = newImage;
        this.imageView.setImage(newImage);
        this.rectangle.setFill(new ImagePattern(newImage));
    }
}