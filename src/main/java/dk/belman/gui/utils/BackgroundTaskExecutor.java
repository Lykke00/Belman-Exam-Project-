package dk.belman.gui.utils;

import javafx.application.Platform;
import javafx.concurrent.Task;

import java.util.concurrent.CompletionException;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class BackgroundTaskExecutor {
    /**
     * Runs a database operation in the background and updates the UI thread when finished.
     * @param backgroundTask The operation to execute (e.g., database call).
     * @param onSuccess The function to execute on the UI thread when successful.
     * @param onFailure The function to execute if an error occurs.
     * @param <T> The type of data returned by the task.
     */
    public static <T> void execute(Supplier<T> backgroundTask, Consumer<T> onSuccess, Consumer<Exception> onFailure) {
        execute(backgroundTask, onSuccess, onFailure, null); // Default: no onLoading
    }

    /**
     * Runs a database operation in the background and updates the UI thread when finished.
     * @param backgroundTask The operation to execute (e.g., database call).
     * @param onSuccess The function to execute on the UI thread when successful.
     * @param onFailure The function to execute if an error occurs.
     * @param onLoading The function to handle the loading state (optional).
     * @param <T> The type of data returned by the task.
     */
    public static <T> void execute(Supplier<T> backgroundTask, Consumer<T> onSuccess, Consumer<Exception> onFailure, Consumer<Boolean> onLoading) {
        if (onLoading != null) {
            onLoading.accept(true); // Indicate loading started
        }

        Task<T> task = new Task<>() {
            @Override
            protected T call() {
                return backgroundTask.get(); // Run the background task
            }
        };

        // Success handling
        task.setOnSucceeded(event -> {
            T result = task.getValue();
            if (result != null) {
                Platform.runLater(() -> onSuccess.accept(result)); // Ensure UI updates happen on JavaFX thread
            } else {
                Platform.runLater(() -> onFailure.accept(new Exception("No data returned")));
            }
            // End loading
            if (onLoading != null) {
                Platform.runLater(() -> onLoading.accept(false));
            }
        });

        // Failure handling
        task.setOnFailed(event -> {
            Throwable error = task.getException();
            Platform.runLater(() -> {
                if (error instanceof Exception exception) {
                    onFailure.accept(exception);
                } else {
                    onFailure.accept(new Exception("Unknown error occurred", error));
                }
                // End loading
                if (onLoading != null) {
                    onLoading.accept(false);
                }
            });
        });

        // Start the task in a new thread
        Thread thread = new Thread(task);
        thread.setDaemon(true); // Makes the thread not block application shutdown
        thread.start();
    }

    /**
     * A cleaner version that handles exceptions internally.
     * The backgroundTask can throw exceptions which will be automatically passed to onFailure.
     */
    public static <T> void executeWithExceptionHandling(
            ExceptionThrowingSupplier<T> backgroundTask,
            Consumer<T> onSuccess,
            Consumer<Exception> onFailure) {

        executeWithExceptionHandling(backgroundTask, onSuccess, onFailure, null);
    }

    /**
     * A cleaner version that handles exceptions internally with loading state support.
     * The backgroundTask can throw exceptions which will be automatically passed to onFailure.
     */
    public static <T> void executeWithExceptionHandling(
            ExceptionThrowingSupplier<T> backgroundTask,
            Consumer<T> onSuccess,
            Consumer<Exception> onFailure,
            Consumer<Boolean> onLoading) {

        if (onLoading != null) {
            onLoading.accept(true);
        }

        Task<T> task = new Task<>() {
            @Override
            protected T call() {
                try {
                    return backgroundTask.get();
                } catch (Exception e) {
                    // We catch it here but will rethrow so the setOnFailed handler gets it
                    throw new CompletionException(e);
                }
            }
        };

        // Success handling
        task.setOnSucceeded(event -> {
            T result = task.getValue();
            Platform.runLater(() -> {
                if (result != null) {
                    onSuccess.accept(result);
                } else {
                    onFailure.accept(new Exception("No data returned"));
                }
                if (onLoading != null) {
                    onLoading.accept(false);
                }
            });
        });

        // Failure handling
        task.setOnFailed(event -> {
            Throwable error = task.getException();
            if (error instanceof CompletionException && error.getCause() instanceof Exception) {
                error = error.getCause();
            }

            final Throwable finalError = error;
            Platform.runLater(() -> {
                if (finalError instanceof Exception exception) {
                    onFailure.accept(exception);
                } else {
                    onFailure.accept(new Exception("Unknown error occurred", finalError));
                }
                if (onLoading != null) {
                    onLoading.accept(false);
                }
            });
        });

        // Start the task in a new thread
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Functional interface that allows exceptions to be thrown
     */
    @FunctionalInterface
    public interface ExceptionThrowingSupplier<T> {
        T get() throws Exception;
    }
}