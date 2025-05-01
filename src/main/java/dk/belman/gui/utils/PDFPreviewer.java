package dk.belman.gui.utils;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.scene.control.ScrollPane;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.File;

public class PDFPreviewer extends ScrollPane {

    private VBox contentBox = new VBox(20);
    private PDDocument document;

    public PDFPreviewer(File pdfFile) {
        setPadding(new Insets(10));
        contentBox.setPadding(new Insets(10));
        contentBox.setAlignment(Pos.CENTER);

        contentBox.setStyle("-fx-background-color: lightgray;");
        this.setStyle("--fx-background-color: lightgray;");

        setContent(contentBox);

        try {
            loadPDF(pdfFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadPDF(File file) throws Exception {
        document = PDDocument.load(file);
        PDFRenderer renderer = new PDFRenderer(document);

        for (int i = 0; i < document.getNumberOfPages(); i++) {
            BufferedImage bim = renderer.renderImageWithDPI(i, 120);
            WritableImage fxImage = SwingFXUtils.toFXImage(bim, null);

            ImageView imageView = new ImageView(fxImage);
            imageView.setPreserveRatio(true);
            imageView.setFitWidth(625);

            Label pageNumber = new Label("Side " + (i + 1));
            pageNumber.setStyle("-fx-font-size: 14px; -fx-text-fill: gray;");

            VBox pageBox = new VBox(5, imageView, pageNumber);
            pageBox.setAlignment(Pos.CENTER);
            pageBox.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-border-color: lightgray;");
            contentBox.getChildren().add(pageBox);
        }

        document.close();
    }
}

