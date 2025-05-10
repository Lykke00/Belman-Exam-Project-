package dk.belman.gui.utils;

import ar.com.hjg.pngj.ImageInfo;
import ar.com.hjg.pngj.ImageLineByte;
import ar.com.hjg.pngj.PngWriter;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class ImageUtilities {
    public static byte[] convertImageToPngBytes(javafx.scene.image.Image image) {
        PixelReader pixelReader = image.getPixelReader();
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        byte[] bgra = new byte[width * height * 4];
        pixelReader.getPixels(0, 0, width, height, PixelFormat.getByteBgraInstance(), bgra, 0, width * 4);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageInfo info = new ImageInfo(width, height, 8, true);
        PngWriter writer = new PngWriter(baos, info);

        for (int row = 0; row < height; row++) {
            ImageLineByte line = new ImageLineByte(info);
            byte[] scanline = line.getScanline();
            for (int col = 0; col < width; col++) {
                int srcIdx = (row * width + col) * 4;
                // BGRA (JavaFX) -> RGBA (PNG)
                scanline[col * 4    ] = bgra[srcIdx + 2]; // R
                scanline[col * 4 + 1] = bgra[srcIdx + 1]; // G
                scanline[col * 4 + 2] = bgra[srcIdx    ]; // B
                scanline[col * 4 + 3] = bgra[srcIdx + 3]; // A
            }
            writer.writeRow(line, row);
        }
        writer.end();
        return baos.toByteArray();
    }

    public static Image convertPngBytesToImage(byte[] pngBytes) {
        return new Image(new ByteArrayInputStream(pngBytes));
    }
}
