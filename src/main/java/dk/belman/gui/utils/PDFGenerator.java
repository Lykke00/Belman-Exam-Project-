package dk.belman.gui.utils;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Image;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;

public class PDFGenerator {

    public static byte[] generatePdfWithImages(String id, String workerName, List<byte[]> imageByteArrays) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc, PageSize.A4);

            float margin = 50;
            float maxWidth = PageSize.A4.getWidth() - 2 * margin;

            document.add(new Paragraph("ID: " + id).setBold().setFontSize(16));
            document.add(new Paragraph("Worker: " + workerName).setFontSize(12));
            document.add(new Paragraph("Date: " + LocalDate.now()).setFontSize(12));

            for (byte[] imgData : imageByteArrays) {
                try {
                    Image img = new Image(ImageDataFactory.create(imgData));
                    float originalWidth = img.getImageWidth();
                    float originalHeight = img.getImageHeight();

                    if (originalWidth > maxWidth) {
                        float scale = maxWidth / originalWidth;
                        img.scaleAbsolute(maxWidth, originalHeight * scale);
                    }

                    document.add(img);
                    document.add(new Paragraph("\n"));
                } catch (Exception e) {
                    System.err.println("Image error: " + e.getMessage());
                }
            }

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            System.err.println("PDF generation error: " + e.getMessage());
            return new byte[0];
        }
    }
}
