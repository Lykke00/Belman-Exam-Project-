package dk.belman.gui.components;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.util.Duration;

/**
 * A custom snackbar implementation for JavaFX and GluonFX applications.
 * Displays different types of notifications (success, error, information)
 * with animated circular indicators and text below.
 */
public class Snackbar {

    // Constants for styling and animation
    private static final double CIRCLE_RADIUS = 30;
    private static final double STROKE_WIDTH = 4;
    private static final double ANIMATION_DURATION = 400; // ms - even faster animation
    private static final double DISPLAY_DURATION = 3000; // ms

    // Success animation path elements - more natural checkmark
    private static final double CHECK_START_X = -CIRCLE_RADIUS * 0.45;
    private static final double CHECK_START_Y = -CIRCLE_RADIUS * 0.1;
    private static final double CHECK_MID_X = -CIRCLE_RADIUS * 0.05;
    private static final double CHECK_MID_Y = CIRCLE_RADIUS * 0.3;
    private static final double CHECK_END_X = CIRCLE_RADIUS * 0.45;
    private static final double CHECK_END_Y = -CIRCLE_RADIUS * 0.3;

    // Error animation path elements
    private static final double X_START_OFFSET = CIRCLE_RADIUS / 2;

    // Colors with opacity
    private static final Color SUCCESS_COLOR = Color.rgb(76, 175, 80, 0.85);
    private static final Color ERROR_COLOR = Color.rgb(244, 67, 54, 0.85);
    private static final Color INFO_COLOR = Color.rgb(255, 193, 7, 0.9);

    // Static reference to the main stackpane
    private static StackPane mainStackPane;

    /**
     * Set the main StackPane where snackbars will be displayed
     * @param stackPane The main StackPane of the application
     */
    public static void setStackPane(StackPane stackPane) {
        mainStackPane = stackPane;
    }

    /**
     * Shows a success snackbar with a checkmark animation
     * @param message The message to display
     */
    public static void showSuccess(String message) {
        if (mainStackPane == null) {
            throw new IllegalStateException("Main StackPane not set. Call setStackPane first.");
        }

        Platform.runLater(() -> {
            VBox container = createBaseContainer(message, SUCCESS_COLOR);
            Circle circle = (Circle) ((Group) container.getChildren().get(0)).getChildren().get(0);
            Group indicatorGroup = (Group) container.getChildren().get(0);

            // Create checkmark path
            Line checkLine1 = new Line(CHECK_START_X, CHECK_START_Y, CHECK_MID_X, CHECK_MID_Y);
            Line checkLine2 = new Line(CHECK_MID_X, CHECK_MID_Y, CHECK_END_X, CHECK_END_Y);

            // Style the lines
            configureAnimatedLine(checkLine1, SUCCESS_COLOR);
            configureAnimatedLine(checkLine2, SUCCESS_COLOR);

            // Add lines to the indicator group
            indicatorGroup.getChildren().addAll(checkLine1, checkLine2);

            // Show the snackbar
            showSnackbar(container);

            // Animate the checkmark with improved fluidity
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(checkLine1.endXProperty(), CHECK_START_X, javafx.animation.Interpolator.EASE_OUT),
                            new KeyValue(checkLine1.endYProperty(), CHECK_START_Y, javafx.animation.Interpolator.EASE_OUT),
                            new KeyValue(checkLine2.endXProperty(), CHECK_MID_X, javafx.animation.Interpolator.EASE_OUT),
                            new KeyValue(checkLine2.endYProperty(), CHECK_MID_Y, javafx.animation.Interpolator.EASE_OUT)
                    ),
                    new KeyFrame(Duration.millis(ANIMATION_DURATION * 0.3), // Faster first segment
                            new KeyValue(checkLine1.endXProperty(), CHECK_MID_X, javafx.animation.Interpolator.EASE_OUT),
                            new KeyValue(checkLine1.endYProperty(), CHECK_MID_Y, javafx.animation.Interpolator.EASE_OUT)
                    ),
                    new KeyFrame(Duration.millis(ANIMATION_DURATION), // Second segment follows immediately
                            new KeyValue(checkLine2.endXProperty(), CHECK_END_X, javafx.animation.Interpolator.EASE_OUT),
                            new KeyValue(checkLine2.endYProperty(), CHECK_END_Y, javafx.animation.Interpolator.EASE_OUT)
                    )
            );

            timeline.play();
        });
    }

    /**
     * Shows an error snackbar with an X animation
     * @param message The message to display
     */
    public static void showError(String message) {
        if (mainStackPane == null) {
            throw new IllegalStateException("Main StackPane not set. Call setStackPane first.");
        }

        Platform.runLater(() -> {
            VBox container = createBaseContainer(message, ERROR_COLOR);
            Circle circle = (Circle) ((Group) container.getChildren().get(0)).getChildren().get(0);
            Group indicatorGroup = (Group) container.getChildren().get(0);

            // Create X path
            Line xLine1 = new Line(-X_START_OFFSET, -X_START_OFFSET, -X_START_OFFSET, -X_START_OFFSET);
            Line xLine2 = new Line(X_START_OFFSET, -X_START_OFFSET, X_START_OFFSET, -X_START_OFFSET);

            // Style the lines
            configureAnimatedLine(xLine1, ERROR_COLOR);
            configureAnimatedLine(xLine2, ERROR_COLOR);

            // Add lines to the indicator group
            indicatorGroup.getChildren().addAll(xLine1, xLine2);

            // Show the snackbar
            showSnackbar(container);

            // Animate the X with improved fluidity
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(xLine1.endXProperty(), -X_START_OFFSET, javafx.animation.Interpolator.EASE_OUT),
                            new KeyValue(xLine1.endYProperty(), -X_START_OFFSET, javafx.animation.Interpolator.EASE_OUT),
                            new KeyValue(xLine2.endXProperty(), X_START_OFFSET, javafx.animation.Interpolator.EASE_OUT),
                            new KeyValue(xLine2.endYProperty(), -X_START_OFFSET, javafx.animation.Interpolator.EASE_OUT)
                    ),
                    new KeyFrame(Duration.millis(ANIMATION_DURATION),
                            new KeyValue(xLine1.endXProperty(), X_START_OFFSET, javafx.animation.Interpolator.EASE_OUT),
                            new KeyValue(xLine1.endYProperty(), X_START_OFFSET, javafx.animation.Interpolator.EASE_OUT),
                            new KeyValue(xLine2.endXProperty(), -X_START_OFFSET, javafx.animation.Interpolator.EASE_OUT),
                            new KeyValue(xLine2.endYProperty(), X_START_OFFSET, javafx.animation.Interpolator.EASE_OUT)
                    )
            );

            timeline.play();
        });
    }

    /**
     * Shows an information snackbar with an i indicator
     * @param message The message to display
     */
    public static void showInfo(String message) {
        if (mainStackPane == null) {
            throw new IllegalStateException("Main StackPane not set. Call setStackPane first.");
        }

        Platform.runLater(() -> {
            VBox container = createBaseContainer(message, INFO_COLOR);
            Circle circle = (Circle) ((Group) container.getChildren().get(0)).getChildren().get(0);
            Group indicatorGroup = (Group) container.getChildren().get(0);

            // Create info symbol (i)
            Line iLine = new Line(0, -CIRCLE_RADIUS/3, 0, CIRCLE_RADIUS/2);
            Circle iDot = new Circle(0, -CIRCLE_RADIUS/2, STROKE_WIDTH/2);

            // Style the elements
            configureAnimatedLine(iLine, INFO_COLOR);
            iDot.setFill(INFO_COLOR);

            // Add elements to the indicator group
            indicatorGroup.getChildren().addAll(iLine, iDot);

            // Show the snackbar
            showSnackbar(container);

            // Animate the i (fade in with additional scaling animation)
            iLine.setOpacity(0);
            iDot.setOpacity(0);
            iLine.setScaleY(0.7);
            iDot.setScaleX(0.7);
            iDot.setScaleY(0.7);

            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(iLine.opacityProperty(), 0, javafx.animation.Interpolator.EASE_OUT),
                            new KeyValue(iDot.opacityProperty(), 0, javafx.animation.Interpolator.EASE_OUT),
                            new KeyValue(iLine.scaleYProperty(), 0.7, javafx.animation.Interpolator.EASE_OUT),
                            new KeyValue(iDot.scaleXProperty(), 0.7, javafx.animation.Interpolator.EASE_OUT),
                            new KeyValue(iDot.scaleYProperty(), 0.7, javafx.animation.Interpolator.EASE_OUT)
                    ),
                    new KeyFrame(Duration.millis(ANIMATION_DURATION),
                            new KeyValue(iLine.opacityProperty(), 1, javafx.animation.Interpolator.EASE_OUT),
                            new KeyValue(iDot.opacityProperty(), 1, javafx.animation.Interpolator.EASE_OUT),
                            new KeyValue(iLine.scaleYProperty(), 1.0, javafx.animation.Interpolator.EASE_OUT),
                            new KeyValue(iDot.scaleXProperty(), 1.0, javafx.animation.Interpolator.EASE_OUT),
                            new KeyValue(iDot.scaleYProperty(), 1.0, javafx.animation.Interpolator.EASE_OUT)
                    )
            );

            timeline.play();
        });
    }

    /**
     * Creates the base container for the snackbar
     */
    private static VBox createBaseContainer(String message, Color color) {
        // Create the circular indicator
        Circle circle = new Circle(CIRCLE_RADIUS);
        circle.setFill(Color.WHITE);
        circle.setStroke(color);
        circle.setStrokeWidth(STROKE_WIDTH);

        // Group for the indicator and animations
        Group indicatorGroup = new Group(circle);

        // Text label
        Label label = new Label(message);
        label.setTextFill(Color.WHITE);
        label.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        // Container for the indicator and text
        VBox container = new VBox(10, indicatorGroup, label);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new javafx.geometry.Insets(15));
        container.setStyle(
                "-fx-background-color: rgba(50, 50, 50, 0.85); " +
                        "-fx-background-radius: 15; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.4), 10, 0, 0, 5);"
        );
        container.setMaxWidth(300);
        container.setMaxHeight(200);
        container.setOpacity(0);

        // Position at the bottom center
        StackPane.setAlignment(container, Pos.BOTTOM_CENTER);
        StackPane.setMargin(container, new javafx.geometry.Insets(0, 0, 50, 0));

        return container;
    }

    /**
     * Configures an animated line with the given color
     */
    private static void configureAnimatedLine(Line line, Color color) {
        line.setStroke(color);
        line.setStrokeWidth(STROKE_WIDTH);
        line.setStrokeLineCap(StrokeLineCap.ROUND);
    }

    /**
     * Shows the snackbar with fade-in/fade-out animation
     */
    private static void showSnackbar(VBox container) {
        // Add to the main pane
        mainStackPane.getChildren().add(container);

        // Prepare container for animation
        container.setScaleX(0.9);
        container.setScaleY(0.9);

        // Improved fade in animation with scale
        Timeline fadeIn = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(container.opacityProperty(), 0, javafx.animation.Interpolator.EASE_OUT),
                        new KeyValue(container.scaleXProperty(), 0.9, javafx.animation.Interpolator.EASE_OUT),
                        new KeyValue(container.scaleYProperty(), 0.9, javafx.animation.Interpolator.EASE_OUT)
                ),
                new KeyFrame(Duration.millis(200), // Faster fade in
                        new KeyValue(container.opacityProperty(), 1, javafx.animation.Interpolator.EASE_OUT),
                        new KeyValue(container.scaleXProperty(), 1.0, javafx.animation.Interpolator.EASE_OUT),
                        new KeyValue(container.scaleYProperty(), 1.0, javafx.animation.Interpolator.EASE_OUT)
                )
        );

        // Improved fade out animation
        Timeline fadeOut = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(container.opacityProperty(), 1, javafx.animation.Interpolator.EASE_IN)
                ),
                new KeyFrame(Duration.millis(200), // Faster fade out
                        new KeyValue(container.opacityProperty(), 0, javafx.animation.Interpolator.EASE_IN)
                )
        );

        fadeOut.setOnFinished(e -> mainStackPane.getChildren().remove(container));

        // Play fade in, then wait, then fade out
        fadeIn.play();

        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(Duration.millis(DISPLAY_DURATION));
        pause.setOnFinished(e -> fadeOut.play());
        pause.play();
    }
}