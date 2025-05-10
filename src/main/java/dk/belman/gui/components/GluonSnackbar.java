package dk.belman.gui.components;

import com.gluonhq.charm.glisten.control.Snackbar;

public class GluonSnackbar {
    private static Snackbar snackbar;

    private final static String DEFAULT_ACTION_TEXT = "Hide";

    public static void showSnackbar(String message) {
        if (snackbar == null) {
            snackbar = new Snackbar(message, DEFAULT_ACTION_TEXT, e -> snackbar.cancel());
        } else {
            snackbar.setMessage(message);
            snackbar.setActionText(DEFAULT_ACTION_TEXT);
            snackbar.setOnAction(e -> snackbar.cancel());
        }
        snackbar.show();
    }

    public static void showSnackbar(String message, String actionText, Runnable action) {
        if (snackbar == null) {
            snackbar = new Snackbar(message, actionText, e -> {
                action.run();
                snackbar.cancel();
            });
        } else {
            snackbar.setMessage(message);
            snackbar.setOnAction(e -> {
                action.run();
                snackbar.cancel();
            });
        }
        snackbar.show();
    }

    public static void hideSnackbar() {
        if (snackbar != null) {
            snackbar.cancel();
        }
    }
}
