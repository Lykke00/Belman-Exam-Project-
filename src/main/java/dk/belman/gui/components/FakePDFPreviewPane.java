package dk.belman.gui.components;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * A component that mimics a PDF viewer but actually just renders content
 * in an A4 page layout without generating a real PDF.
 */
public class FakePDFPreviewPane extends BorderPane {

    private static final double A4_WIDTH_PX = 595.0;
    private static final double A4_HEIGHT_PX = 842.0;
    private static final double MARGIN = 50.0;

    private VBox pagesContainer;
    private ScrollPane scrollPane;
    private Label pageLabel;
    private IntegerProperty currentPage = new SimpleIntegerProperty(0);
    private int totalPages = 0;

    private double scaleFactor = 1.0;

    private List<Node> pageNodes = new ArrayList<>();

    public FakePDFPreviewPane() {
        setupUI();
    }

    private void setupUI() {
        pagesContainer = new VBox(20);
        pagesContainer.setAlignment(Pos.TOP_CENTER);
        pagesContainer.setPadding(new Insets(20));
        pagesContainer.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));

        StackPane centeringWrapper = new StackPane(pagesContainer);
        centeringWrapper.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        centeringWrapper.setAlignment(Pos.TOP_CENTER);

        scrollPane = new ScrollPane(centeringWrapper);
        scrollPane.setStyle("-fx-background-color: #F0F0F0FF");
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setBackground(new Background(new BackgroundFill(Color.DARKGRAY, CornerRadii.EMPTY, Insets.EMPTY)));

        StackPane contentStack = new StackPane(scrollPane);
        setCenter(contentStack);

        pageLabel = new Label("0 pages");

        HBox controls = new HBox(10, pageLabel);
        controls.setAlignment(Pos.CENTER);
        controls.setPadding(new Insets(10));
        controls.setStyle("-fx-background-color: #f0f0f0;");
        setBottom(controls);

        currentPage.addListener((obs, oldVal, newVal) -> {
            if (totalPages > 0) {
                pageLabel.setText(totalPages + " pages");
            }
        });

        pagesContainer.setMaxWidth(A4_WIDTH_PX * scaleFactor);
        updatePageSizes();
    }

    private void updatePageSizes() {
        for (Node pageNode : pageNodes) {
            if (pageNode instanceof StackPane) {
                StackPane pagePane = (StackPane) pageNode;
                pagePane.setPrefWidth(A4_WIDTH_PX * scaleFactor);
                pagePane.setPrefHeight(A4_HEIGHT_PX * scaleFactor);

                Rectangle clip = new Rectangle(A4_WIDTH_PX * scaleFactor, A4_HEIGHT_PX * scaleFactor);
                pagePane.setClip(clip);

                pagePane.getChildren().stream()
                        .filter(child -> child instanceof VBox)
                        .findFirst()
                        .ifPresent(contentBox -> {
                            VBox content = (VBox) contentBox;
                            content.setPrefWidth((A4_WIDTH_PX - 2 * MARGIN) * scaleFactor);

                            content.getChildren().stream()
                                    .filter(child -> child instanceof Label)
                                    .forEach(label -> {
                                        Label l = (Label) label;
                                        if (l.getText().startsWith("ID:")) {
                                            l.setFont(Font.font("System", FontWeight.BOLD, 16 * scaleFactor));
                                        } else {
                                            l.setFont(Font.font("System", FontWeight.NORMAL, 12 * scaleFactor));
                                        }
                                    });

                            content.getChildren().stream()
                                    .filter(child -> child instanceof ImageView)
                                    .forEach(imgView -> {
                                        ImageView iv = (ImageView) imgView;
                                        double maxWidth = (A4_WIDTH_PX - 2 * MARGIN) * scaleFactor;

                                        if (iv.getUserData() instanceof double[]) {
                                            double[] originalSize = (double[]) iv.getUserData();
                                            double originalWidth = originalSize[0];
                                            double originalHeight = originalSize[1];

                                            if (originalWidth > (A4_WIDTH_PX - 2 * MARGIN)) {
                                                double scale = (A4_WIDTH_PX - 2 * MARGIN) / originalWidth;
                                                iv.setFitWidth(maxWidth);
                                                iv.setFitHeight(originalHeight * scale * scaleFactor);
                                            } else {
                                                iv.setFitWidth(originalWidth * scaleFactor);
                                                iv.setFitHeight(originalHeight * scaleFactor);
                                            }
                                        }
                                    });
                        });
            }
        }
    }


    public void loadContent(String id, String workerName, List<byte[]> imageByteArrays) {
        pageNodes.clear();
        pagesContainer.getChildren().clear();
        totalPages = 0;
        currentPage.set(0);

        List<Node> pages = new ArrayList<>();

        Label idLabel = new Label("ID: " + id);
        idLabel.setFont(Font.font("System", FontWeight.BOLD, 16));

        Label workerLabel = new Label("Name: " + workerName);
        workerLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));

        Label dateLabel = new Label("Date: " + LocalDate.now());
        dateLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));

        double headerHeight = 80;
        double availablePageHeight = A4_HEIGHT_PX - 2 * MARGIN - headerHeight;
        double currentPageHeight = 0;

        VBox currentPageContent = new VBox(10);
        currentPageContent.setPadding(new Insets(MARGIN));
        currentPageContent.setPrefWidth(A4_WIDTH_PX - 2 * MARGIN);

        currentPageContent.getChildren().addAll(
                clone(idLabel),
                clone(workerLabel),
                clone(dateLabel)
        );
        currentPageHeight = headerHeight;

        float maxWidth = (float)(A4_WIDTH_PX - 2 * MARGIN);

        for (byte[] imgData : imageByteArrays) {
            try {
                Image image = new Image(new ByteArrayInputStream(imgData));
                ImageView imageView = new ImageView(image);
                imageView.setPreserveRatio(true);

                imageView.setUserData(new double[] {image.getWidth(), image.getHeight()});

                double originalWidth = image.getWidth();
                double originalHeight = image.getHeight();

                if (originalWidth > maxWidth) {
                    double scale = maxWidth / originalWidth;
                    imageView.setFitWidth(maxWidth);
                    imageView.setFitHeight(originalHeight * scale);
                } else {
                    imageView.setFitWidth(originalWidth);
                    imageView.setFitHeight(originalHeight);
                }

                double scaledHeight = imageView.getFitHeight();

                if (currentPageHeight + scaledHeight > availablePageHeight) {
                    pages.add(createPageNode(currentPageContent));

                    currentPageContent = new VBox(10);
                    currentPageContent.setPadding(new Insets(MARGIN));
                    currentPageContent.setPrefWidth(A4_WIDTH_PX - 2 * MARGIN);
                    currentPageHeight = 0;
                }

                currentPageContent.getChildren().add(imageView);
                currentPageHeight += scaledHeight + 10;

            } catch (Exception e) {
                System.err.println("Image error: " + e.getMessage());
                Label errorLabel = new Label("Error loading image: " + e.getMessage());
                errorLabel.setStyle("-fx-text-fill: red;");
                currentPageContent.getChildren().add(errorLabel);
            }
        }

        if (!currentPageContent.getChildren().isEmpty())
            pages.add(createPageNode(currentPageContent));

        final List<Node> finalPages = pages;
        pageNodes.addAll(finalPages);
        pagesContainer.getChildren().addAll(finalPages);
        totalPages = finalPages.size();

        if (totalPages > 0) {
            currentPage.set(0);
            pageLabel.setText(totalPages + " pages");
        } else {
            pageLabel.setText("No pages");
        }

        updatePageSizes();
    }

    public void loadContent(String id, String workerName, ObservableList<Image> images) {
        pageNodes.clear();
        pagesContainer.getChildren().clear();
        totalPages = 0;
        currentPage.set(0);

        List<Node> pages = new ArrayList<>();

        Label idLabel = new Label("ID: " + id);
        idLabel.setFont(Font.font("System", FontWeight.BOLD, 16));

        Label workerLabel = new Label("Name: " + workerName);
        workerLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));

        Label dateLabel = new Label("Date: " + LocalDate.now());
        dateLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));

        double headerHeight = 80;
        double availablePageHeight = A4_HEIGHT_PX - 2 * MARGIN - headerHeight;
        double currentPageHeight = 0;

        VBox currentPageContent = new VBox(10);
        currentPageContent.setPadding(new Insets(MARGIN));
        currentPageContent.setPrefWidth(A4_WIDTH_PX - 2 * MARGIN);

        currentPageContent.getChildren().addAll(
                clone(idLabel),
                clone(workerLabel),
                clone(dateLabel)
        );
        currentPageHeight = headerHeight;

        float maxWidth = (float)(A4_WIDTH_PX - 2 * MARGIN);

        for (Image image : images) {
            try {
                ImageView imageView = new ImageView(image);
                imageView.setPreserveRatio(true);

                imageView.setUserData(new double[]{image.getWidth(), image.getHeight()});

                double originalWidth = image.getWidth();
                double originalHeight = image.getHeight();

                if (originalWidth > maxWidth) {
                    double scale = maxWidth / originalWidth;
                    imageView.setFitWidth(maxWidth);
                    imageView.setFitHeight(originalHeight * scale);
                } else {
                    imageView.setFitWidth(originalWidth);
                    imageView.setFitHeight(originalHeight);
                }

                double scaledHeight = imageView.getFitHeight();

                if (currentPageHeight + scaledHeight > availablePageHeight) {
                    pages.add(createPageNode(currentPageContent));

                    currentPageContent = new VBox(10);
                    currentPageContent.setPadding(new Insets(MARGIN));
                    currentPageContent.setPrefWidth(A4_WIDTH_PX - 2 * MARGIN);
                    currentPageHeight = 0;
                }

                currentPageContent.getChildren().add(imageView);
                currentPageHeight += scaledHeight + 10;

            } catch (Exception e) {
                System.err.println("Image error: " + e.getMessage());
                Label errorLabel = new Label("Error loading image: " + e.getMessage());
                errorLabel.setStyle("-fx-text-fill: red;");
                currentPageContent.getChildren().add(errorLabel);
            }
        }

        if (!currentPageContent.getChildren().isEmpty()) {
            pages.add(createPageNode(currentPageContent));
        }

        final List<Node> finalPages = pages;
        pageNodes.addAll(finalPages);
        pagesContainer.getChildren().addAll(finalPages);
        totalPages = finalPages.size();

        if (totalPages > 0) {
            currentPage.set(0);
            pageLabel.setText(totalPages + " pages");
        } else {
            pageLabel.setText("No pages");
        }

        updatePageSizes();
    }


    private Node createPageNode(VBox content) {
        StackPane pagePane = new StackPane();
        pagePane.setAlignment(Pos.TOP_CENTER);
        pagePane.getChildren().add(content);
        pagePane.setPrefSize(A4_WIDTH_PX, A4_HEIGHT_PX);
        pagePane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        pagePane.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 10, 0, 0, 0);");

        Rectangle clip = new Rectangle(A4_WIDTH_PX, A4_HEIGHT_PX);
        pagePane.setClip(clip);

        return pagePane;
    }

    private Label clone(Label original) {
        Label clone = new Label(original.getText());
        clone.setFont(original.getFont());
        clone.setStyle(original.getStyle());
        return clone;
    }

    private void showError(String message) {
        Label errorLabel = new Label(message);
        errorLabel.setStyle("-fx-text-fill: red;");
        VBox errorBox = new VBox(errorLabel);
        errorBox.setAlignment(Pos.CENTER);
        setCenter(errorBox);
    }

    public static FakePDFPreviewPane createPreviewObservable(String id, String workerName, ObservableList<Image> list) {
        FakePDFPreviewPane previewPane = new FakePDFPreviewPane();
        previewPane.loadContent(id, workerName, list);
        return previewPane;
    }

    public static FakePDFPreviewPane createPreview(String id, String workerName, List<byte[]> imageByteArrays) {
        FakePDFPreviewPane previewPane = new FakePDFPreviewPane();
        previewPane.loadContent(id, workerName, imageByteArrays);
        return previewPane;
    }
}
