package dk.belman.gui.components;

import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import dk.belman.gui.utils.IconStyle;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.input.MouseEvent;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;
import javafx.animation.PauseTransition;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;

public class SelectableImageView extends StackPane {
    private boolean isSelected = false;
    private ImageView imageView;
    private PauseTransition longPressPause;
    private Circle selectionCircle;

    private final MaterialDesignIcon checkIcon = MaterialDesignIcon.CHECK;
    private Node selectionIcon = checkIcon.graphic(IconStyle.getIconStyle());

    public SelectableImageView(String imagePath, int width, int height) {
        imageView = new ImageView(new Image(imagePath));
        this.initialize(imageView, width, height);
    }

    public SelectableImageView(Image image, int width, int height) {
        imageView = new ImageView(image);
        this.initialize(imageView, width, height);
    }

    private void initialize(ImageView imageView, int width, int height) {
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);

        StackPane stackPane = new StackPane();

        selectionCircle = new Circle(13);
        selectionCircle.setStroke(Color.WHITE);
        selectionCircle.setStrokeWidth(2);
        selectionCircle.setFill(Color.rgb(255, 255, 255, 0.3));

        stackPane.setPadding(new Insets(5));
        stackPane.setAlignment(Pos.TOP_RIGHT);

        StackPane.setMargin(selectionIcon, new Insets(2, 4, 0, 0));

        selectionIcon.setVisible(false);

        stackPane.getChildren().addAll(selectionCircle, selectionIcon);

        this.setAlignment(Pos.TOP_RIGHT);
        this.getChildren().addAll(imageView, stackPane);

        this.setStyle("-fx-border-color: transparent; -fx-border-width: 3;");
        this.setPickOnBounds(true);
        this.setOnMouseClicked(this::handleClick);

        longPressPause = new PauseTransition(Duration.seconds(0.5));
        longPressPause.setOnFinished(event -> showImageDialog());

        this.setOnMousePressed(this::handleMousePressed);
        this.setOnMouseReleased(this::handleMouseReleased);
    }

    private void handleClick(MouseEvent event) {
        isSelected = !isSelected;

        selectionIcon.setVisible(isSelected);

        if (isSelected) {
            this.setStyle("-fx-border-color: #00539B; -fx-border-width: 3;");
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), this);
            scaleTransition.setToX(1.02);
            scaleTransition.setToY(1.02);
            scaleTransition.play();

            ColorAdjust colorAdjust = new ColorAdjust();
            colorAdjust.setBrightness(-0.3);
            imageView.setEffect(colorAdjust);

        } else {
            this.setStyle("-fx-border-color: transparent; -fx-border-width: 3;");
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), this);
            scaleTransition.setToX(1);
            scaleTransition.setToY(1);
            scaleTransition.play();

            imageView.setEffect(null);
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

    public boolean isSelected() {
        return isSelected;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public Image getImage() {
        return imageView.getImage();
    }
}
