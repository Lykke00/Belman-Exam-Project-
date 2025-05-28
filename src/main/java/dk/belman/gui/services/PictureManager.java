package dk.belman.gui.services;

import com.gluonhq.attach.pictures.PicturesService;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.image.Image;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class PictureManager {
    private static PicturesService picturesService;
    private static ChangeListener<Image> currentListener;

    // Private constructor to prevent instantiation
    private PictureManager() {}

    /**
     * Initialize the picture service
     */
    public static void initialize() {
        if (picturesService == null) {
            picturesService = PicturesService.create().orElse(null);
            if (picturesService == null) {
                throw new RuntimeException("Failed to initialize PicturesService");
            }
        }
    }

    /**
     * Take a picture for normal operations
     * @param onImageReceived Callback when image is received
     */
    public static void takePicture(Consumer<Image> onImageReceived) {
        ensureInitialized();

        // Remove any existing listener
        clearCurrentListener();

        // Set up new listener
        currentListener = (obs, oldImage, newImage) -> {
            if (newImage != null) {
                Platform.runLater(() -> {
                    onImageReceived.accept(newImage);
                    clearCurrentListener(); // Auto-remove after use
                });
            }
        };

        picturesService.imageProperty().addListener(currentListener);
        triggerCamera();
    }

    /**
     * Take a picture for retake operations
     * @param onImageReceived Callback when image is received
     * @return CompletableFuture that completes when image is received
     */
    public static CompletableFuture<Image> retakePicture(Consumer<Image> onImageReceived) {
        ensureInitialized();
        CompletableFuture<Image> future = new CompletableFuture<>();

        // Remove any existing listener
        clearCurrentListener();

        // Set up retake listener
        currentListener = (obs, oldImage, newImage) -> {
            if (newImage != null) {
                Platform.runLater(() -> {
                    onImageReceived.accept(newImage);
                    future.complete(newImage);
                    clearCurrentListener(); // Auto-remove after use
                });
            }
        };

        picturesService.imageProperty().addListener(currentListener);
        triggerCamera();

        return future;
    }

    /**
     * Take a picture with a CompletableFuture return (no callback needed)
     * @return CompletableFuture that completes with the captured image
     */
    public static CompletableFuture<Image> takePictureAsync() {
        ensureInitialized();
        CompletableFuture<Image> future = new CompletableFuture<>();

        // Remove any existing listener
        clearCurrentListener();

        // Set up listener
        currentListener = (obs, oldImage, newImage) -> {
            if (newImage != null) {
                Platform.runLater(() -> {
                    future.complete(newImage);
                    clearCurrentListener(); // Auto-remove after use
                });
            }
        };

        picturesService.imageProperty().addListener(currentListener);
        triggerCamera();

        return future;
    }

    /**
     * Get the current image property for custom listener setup
     * Use this only if you need direct access to the property
     * @return The image property
     */
    public static ReadOnlyObjectProperty<Image> getImageProperty() {
        ensureInitialized();
        return picturesService.imageProperty();
    }

    /**
     * Check if picture service is available
     * @return true if service is available
     */
    public static boolean isAvailable() {
        return picturesService != null;
    }

    /**
     * Cleanup method - call when shutting down
     */
    public static void cleanup() {
        clearCurrentListener();
        if (picturesService != null) {
            // Add any cleanup logic for picturesService if needed
            picturesService = null;
        }
    }

    // Private helper methods

    private static void ensureInitialized() {
        if (picturesService == null) {
            initialize();
        }
    }

    private static void clearCurrentListener() {
        if (currentListener != null && picturesService != null) {
            picturesService.imageProperty().removeListener(currentListener);
            currentListener = null;
        }
    }

    private static void triggerCamera() {
        // Add your camera triggering logic here
        // This might be a method call to open camera, or platform-specific code
        System.out.println("Triggering camera...");
        // Example: if you have a camera service
        // CameraService.openCamera();
    }
}