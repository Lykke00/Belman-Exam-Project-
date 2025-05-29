package dk.belman.gui.utils;

import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.Canvas;

import dk.belman.gui.common.PictureItemModel;
import javafx.collections.ObservableList;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;

public class PDFGenerator {

    private static final float MARGIN = 50f;
    private static final float MAX_WIDTH = PageSize.A4.getWidth() - 2 * MARGIN;
    private static final float HEADER_HEIGHT = 80f;
    private static final float AVAILABLE_PAGE_HEIGHT = PageSize.A4.getHeight() - 2 * MARGIN - HEADER_HEIGHT;

    public static byte[] generatePdfWithImages(String id, String workerName, ObservableList<PictureItemModel> pictureItems) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc, PageSize.A4);
            document.setMargins(MARGIN, MARGIN, MARGIN, MARGIN);

            // Track current page height for page breaks
            float currentPageHeight = 0;
            boolean isFirstPage = true;

            // Add header to first page
            addHeader(document, id, workerName);
            currentPageHeight = HEADER_HEIGHT;

            for (PictureItemModel pictureItem : pictureItems) {
                try {
                    // Get image data
                    javafx.scene.image.Image fxImage = pictureItem.pictureProperty().get();
                    if (fxImage == null) continue;

                    // Convert JavaFX Image to byte array (you'll need to implement this method)
                    byte[] imageBytes = convertFxImageToBytes(fxImage);
                    if (imageBytes == null || imageBytes.length == 0) continue;

                    // Create state label
                    String stateText = "State: " + pictureItem.stateProperty().get().toString();
                    Paragraph stateParagraph = new Paragraph(stateText)
                            .setBold()
                            .setFontSize(14)
                            .setMarginBottom(5);

                    // Create image
                    Image img = new Image(ImageDataFactory.create(imageBytes));
                    float originalWidth = img.getImageWidth();
                    float originalHeight = img.getImageHeight();

                    // Scale image if needed
                    if (originalWidth > MAX_WIDTH) {
                        float scale = MAX_WIDTH / originalWidth;
                        img.scaleAbsolute(MAX_WIDTH, originalHeight * scale);
                    } else {
                        img.scaleAbsolute(originalWidth, originalHeight);
                    }

                    // Create comment label
                    String commentText = pictureItem.commentProperty().get();
                    if (commentText == null || commentText.trim().isEmpty()) {
                        commentText = "No comment";
                    }
                    Paragraph commentParagraph = new Paragraph("Comment: " + commentText)
                            .setFontSize(12)
                            .setMarginTop(5)
                            .setMarginBottom(10);

                    // Create separator
                    LineSeparator separator = new LineSeparator(new SolidLine(0.5f))
                            .setMarginTop(10)
                            .setMarginBottom(10);

                    // Calculate total height needed for this item
                    float stateHeight = 20; // Approximate
                    float imageHeight = img.getImageScaledHeight();
                    float commentHeight = 20; // Approximate
                    float separatorHeight = 21; // 10 + 0.5 + 10
                    float totalItemHeight = stateHeight + imageHeight + commentHeight + separatorHeight;

                    // Check if we need a new page
                    if (!isFirstPage && currentPageHeight + totalItemHeight > AVAILABLE_PAGE_HEIGHT) {
                        document.add(new AreaBreak());
                        currentPageHeight = 0;
                    }

                    // Add elements to document
                    document.add(stateParagraph);
                    document.add(img);
                    document.add(commentParagraph);
                    document.add(separator);

                    currentPageHeight += totalItemHeight;
                    isFirstPage = false;

                } catch (Exception e) {
                    System.err.println("Error processing image: " + e.getMessage());
                    // Add error message to PDF
                    document.add(new Paragraph("Error loading image: " + e.getMessage())
                            .setFontColor(ColorConstants.RED)
                            .setFontSize(12));
                }
            }

            // Add page numbers to all pages
            addPageNumbers(pdfDoc);

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            System.err.println("PDF generation error: " + e.getMessage());
            return new byte[0];
        }
    }

    private static void addHeader(Document document, String id, String workerName) {
        document.add(new Paragraph("ID: " + id)
                .setBold()
                .setFontSize(16)
                .setMarginBottom(5));

        document.add(new Paragraph("Name: " + workerName)
                .setFontSize(12)
                .setMarginBottom(5));

        document.add(new Paragraph("Date: " + LocalDate.now())
                .setFontSize(12)
                .setMarginBottom(20));
    }


    private static void addPageNumbers(PdfDocument pdfDoc) {
        int totalPages = pdfDoc.getNumberOfPages();

        try {
            var font = PdfFontFactory.createFont(); // Brug default font

            for (int i = 1; i <= totalPages; i++) {
                var page = pdfDoc.getPage(i);
                var pageSize = page.getPageSize();

                PdfCanvas canvas = new PdfCanvas(page);
                canvas.beginText()
                        .setFontAndSize(font, 10)
                        .moveText(pageSize.getWidth() - MARGIN - 40, MARGIN - 10)
                        .showText("Page " + i + " of " + totalPages)
                        .endText();
            }
        } catch (Exception e) {
            System.err.println("Page number rendering failed: " + e.getMessage());
        }
    }



    /**
     * Convert JavaFX Image to byte array
     * You'll need to implement this method based on your image handling
     */
    private static byte[] convertFxImageToBytes(javafx.scene.image.Image fxImage) {
        try {
            // This is a placeholder - you'll need to implement the actual conversion
            // One approach is to use JavaFX's SwingFXUtils and then convert BufferedImage to bytes

            java.awt.image.BufferedImage bufferedImage = javafx.embed.swing.SwingFXUtils.fromFXImage(fxImage, null);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            javax.imageio.ImageIO.write(bufferedImage, "png", baos);
            return baos.toByteArray();

        } catch (Exception e) {
            System.err.println("Error converting JavaFX image to bytes: " + e.getMessage());
            return null;
        }
    }

    // Alternative method if you have byte arrays directly
    public static byte[] generatePdfWithImageBytes(String id, String workerName, List<ImageItem> imageItems) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc, PageSize.A4);
            document.setMargins(MARGIN, MARGIN, MARGIN, MARGIN);

            float currentPageHeight = 0;
            boolean isFirstPage = true;

            // Add header to first page
            addHeader(document, id, workerName);
            currentPageHeight = HEADER_HEIGHT;

            for (ImageItem imageItem : imageItems) {
                try {
                    // Create state label
                    Paragraph stateParagraph = new Paragraph("State: " + imageItem.state())
                            .setBold()
                            .setFontSize(14)
                            .setMarginBottom(5);

                    // Create image
                    Image img = new Image(ImageDataFactory.create(imageItem.imageBytes()));
                    float originalWidth = img.getImageWidth();
                    float originalHeight = img.getImageHeight();

                    // Scale image if needed
                    if (originalWidth > MAX_WIDTH) {
                        float scale = MAX_WIDTH / originalWidth;
                        img.scaleAbsolute(MAX_WIDTH, originalHeight * scale);
                    } else {
                        img.scaleAbsolute(originalWidth, originalHeight);
                    }

                    // Create comment label
                    String commentText = imageItem.comment();
                    if (commentText == null || commentText.trim().isEmpty()) {
                        commentText = "No comment";
                    }
                    Paragraph commentParagraph = new Paragraph("Comment: " + commentText)
                            .setFontSize(12)
                            .setMarginTop(5)
                            .setMarginBottom(10);

                    // Create separator
                    LineSeparator separator = new LineSeparator(new SolidLine(0.5f))
                            .setMarginTop(10)
                            .setMarginBottom(10);

                    // Calculate total height needed
                    float totalItemHeight = 20 + img.getImageScaledHeight() + 20 + 21;

                    // Check if we need a new page
                    if (!isFirstPage && currentPageHeight + totalItemHeight > AVAILABLE_PAGE_HEIGHT) {
                        document.add(new AreaBreak());
                        currentPageHeight = 0;
                    }

                    // Add elements
                    document.add(stateParagraph);
                    document.add(img);
                    document.add(commentParagraph);
                    document.add(separator);

                    currentPageHeight += totalItemHeight;
                    isFirstPage = false;

                } catch (Exception e) {
                    System.err.println("Error processing image: " + e.getMessage());
                    document.add(new Paragraph("Error loading image: " + e.getMessage())
                            .setFontColor(ColorConstants.RED)
                            .setFontSize(12));
                }
            }

            // Add page numbers
            addPageNumbers(pdfDoc);

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            System.err.println("PDF generation error: " + e.getMessage());
            return new byte[0];
        }
    }

    // Helper class for image data
        public record ImageItem(byte[] imageBytes, String state, String comment) {
    }
}