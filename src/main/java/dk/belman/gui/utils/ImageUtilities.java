package dk.belman.gui.utils;

import ar.com.hjg.pngj.ImageInfo;
import ar.com.hjg.pngj.ImageLineByte;
import ar.com.hjg.pngj.PngWriter;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritablePixelFormat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

public class ImageUtilities {
    public static byte[] convertImageToPngBytes(Image image) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        // Pre-allocate a direct buffer for the pixel data
        // This reduces copying and GC pressure
        ByteBuffer buffer = ByteBuffer.allocateDirect(width * height * 4);

        // Get all pixels at once - more efficient than pixel by pixel
        image.getPixelReader().getPixels(0, 0, width, height,
                PixelFormat.getByteBgraInstance(),
                buffer, width * 4);

        // Create PNG writer with optimized settings
        ByteArrayOutputStream baos = new ByteArrayOutputStream(width * height * 3); // Estimate size
        ImageInfo info = new ImageInfo(width, height, 8, true);
        PngWriter writer = new PngWriter(baos, info);

        // Set compression level - adjust based on your needs
        // Higher values = better compression but slower processing
        writer.setCompLevel(3);

        // Disable unnecessary metadata
        writer.getMetadata().setDpi(0, 0);

        // Process entire rows at once instead of pixel by pixel
        byte[] rgbaRow = new byte[width * 4];
        buffer.position(0);

        for (int row = 0; row < height; row++) {
            ImageLineByte line = new ImageLineByte(info);
            byte[] scanline = line.getScanline();

            // Get a full row of BGRA data
            buffer.get(rgbaRow);

            // Convert BGRA to RGBA in bulk (faster than pixel by pixel)
            for (int col = 0; col < width; col++) {
                int srcIdx = col * 4;
                scanline[srcIdx    ] = rgbaRow[srcIdx + 2]; // R
                scanline[srcIdx + 1] = rgbaRow[srcIdx + 1]; // G
                scanline[srcIdx + 2] = rgbaRow[srcIdx    ]; // B
                scanline[srcIdx + 3] = rgbaRow[srcIdx + 3]; // A
            }

            writer.writeRow(line, row);

            // Reset buffer position for next row
            buffer.position((row + 1) * width * 4);
        }

        writer.end();
        return baos.toByteArray();
    }

    // Alternative implementation using parallel processing for larger images
    public static byte[] convertImageToPngBytesParallel(Image image) {
        final int width = (int) image.getWidth();
        final int height = (int) image.getHeight();

        // Only use parallel processing for larger images
        if (width * height < 1_000_000) { // Less than 1M pixels
            return convertImageToPngBytes(image);
        }

        // Get all pixels at once
        int[] pixelData = new int[width * height];
        image.getPixelReader().getPixels(0, 0, width, height,
                WritablePixelFormat.getIntArgbInstance(),
                pixelData, 0, width);

        // Pre-process all scan lines in parallel
        ImageInfo info = new ImageInfo(width, height, 8, true);
        byte[][] allScanlines = new byte[height][];

        int threadCount = Math.min(Runtime.getRuntime().availableProcessors(), height);
        int rowsPerThread = height / threadCount;

        Thread[] threads = new Thread[threadCount];
        for (int t = 0; t < threadCount; t++) {
            final int startRow = t * rowsPerThread;
            final int endRow = (t == threadCount - 1) ? height : startRow + rowsPerThread;

            threads[t] = new Thread(() -> {
                for (int row = startRow; row < endRow; row++) {
                    byte[] scanline = new byte[width * 4];
                    for (int col = 0; col < width; col++) {
                        int argb = pixelData[row * width + col];
                        int baseIdx = col * 4;
                        // ARGB (int) -> RGBA (PNG)
                        scanline[baseIdx    ] = (byte)((argb >> 16) & 0xFF); // R
                        scanline[baseIdx + 1] = (byte)((argb >> 8) & 0xFF);  // G
                        scanline[baseIdx + 2] = (byte)(argb & 0xFF);         // B
                        scanline[baseIdx + 3] = (byte)((argb >> 24) & 0xFF); // A
                    }
                    allScanlines[row] = scanline;
                }
            });
            threads[t].start();
        }

        // Wait for all threads to complete
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Write the PNG in a single thread
        ByteArrayOutputStream baos = new ByteArrayOutputStream(width * height * 3);
        PngWriter writer = new PngWriter(baos, info);
        writer.setCompLevel(3);
        writer.getMetadata().setDpi(0, 0);

        for (int row = 0; row < height; row++) {
            ImageLineByte line = new ImageLineByte(info);
            System.arraycopy(allScanlines[row], 0, line.getScanline(), 0, width * 4);
            writer.writeRow(line, row);
        }

        writer.end();
        return baos.toByteArray();
    }

    public static Image convertPngBytesToImage(byte[] pngBytes) {
        return new Image(new ByteArrayInputStream(pngBytes));
    }
}
