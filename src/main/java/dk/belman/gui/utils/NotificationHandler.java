package easv.event.gui.utils;

import atlantafx.base.controls.Notification;
import atlantafx.base.theme.Styles;
import atlantafx.base.util.Animations;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2OutlinedAL;

import java.util.ArrayList;
import java.util.List;

public class NotificationHandler {
    private static NotificationHandler instance;
    private final StackPane notificationContainer;
    private final List<Notification> notifications;

    private static final int MAX_NOTIFICATIONS = 4;
    private static final double NOTIFICATION_MARGIN = 10;
    private static final Duration ANIMATION_DURATION = Duration.millis(200);
    private static final Duration NOTIFICATION_DURATION = Duration.seconds(3);

    private NotificationHandler(StackPane notificationContainer) {
        this.notificationContainer = notificationContainer;
        this.notifications = new ArrayList<>();
    }

    public static void initialize(StackPane notificationContainer) {
        if (instance == null) {
            instance = new NotificationHandler(notificationContainer);
        }
    }

    public static NotificationHandler getInstance() {
        if (instance == null) {
            throw new IllegalStateException("NotificationManager is not initialized. Call initialize() first.");
        }
        return instance;
    }

    public enum NotificationType {
        INFO,
        SUCCESS,
        WARNING,
        DANGER
    }

    public void showNotification(String message, NotificationType type) {
        if (notifications.size() >= MAX_NOTIFICATIONS) {
            removeOldestNotification();
        }

        FontIcon icon;
        switch (type) {
            case SUCCESS:
                icon = new FontIcon(Material2OutlinedAL.CHECK_CIRCLE_OUTLINE);
                break;
            case WARNING:
                icon = new FontIcon(Material2OutlinedAL.HOW_TO_VOTE);
                break;
            case DANGER:
                icon = new FontIcon(Material2OutlinedAL.ERROR_OUTLINE);
                break;
            case INFO:
            default:
                icon = new FontIcon(Material2OutlinedAL.HELP_OUTLINE);
                break;
        }

        final var notification = new Notification(message, icon);
        notification.getStyleClass().add(Styles.ELEVATED_1);

        switch (type) {
            case INFO:
                notification.getStyleClass().add(Styles.ACCENT);
                break;
            case SUCCESS:
                notification.getStyleClass().add(Styles.SUCCESS);
                break;
            case WARNING:
                notification.getStyleClass().add(Styles.WARNING);
                break;
            case DANGER:
                notification.getStyleClass().add(Styles.DANGER);
                break;
        }

        notification.setPrefHeight(Region.USE_PREF_SIZE);
        notification.setMaxHeight(Region.USE_PREF_SIZE);
        notification.setMaxWidth(300);

        double totalHeight = notifications.stream()
                .mapToDouble(n -> n.getHeight() + NOTIFICATION_MARGIN)
                .sum();

        StackPane.setAlignment(notification, Pos.TOP_RIGHT);
        StackPane.setMargin(notification, new Insets(totalHeight + NOTIFICATION_MARGIN, 10, 0, 0));

        notification.setOpacity(0);
        notification.setScaleX(0.8);
        notification.setScaleY(0.8);

        notificationContainer.getChildren().add(notification);
        notifications.add(notification);

        playEntranceAnimation(notification);

        notification.setOnClose(e -> removeNotification(notification));

        PauseTransition pause = new PauseTransition(NOTIFICATION_DURATION);
        pause.setOnFinished(event -> removeNotification(notification));
        pause.play();
    }

    private void removeOldestNotification() {
        if (!notifications.isEmpty()) {
            Notification oldestNotification = notifications.get(0);
            removeNotification(oldestNotification);
        }
    }

    private void removeNotification(Notification notification) {
        playExitAnimation(notification, () -> {
            notificationContainer.getChildren().remove(notification);
            notifications.remove(notification);

            animateRemainingNotifications();
        });
    }

    private void animateRemainingNotifications() {
        double totalHeight = 0;

        for (Notification notification : notifications) {
            double targetY = totalHeight + NOTIFICATION_MARGIN;
            Insets currentMargin = StackPane.getMargin(notification);
            double startY = currentMargin != null ? currentMargin.getTop() : 0;

            Timeline timeline = new Timeline();
            KeyValue kv = new KeyValue(notification.translateYProperty(), targetY - startY);
            KeyFrame kf = new KeyFrame(ANIMATION_DURATION, kv);
            timeline.getKeyFrames().add(kf);

            timeline.setOnFinished(e -> {
                notification.setTranslateY(0);
                StackPane.setMargin(notification, new Insets(targetY, 10, 0, 0));
            });

            timeline.play();

            totalHeight += notification.getHeight() + NOTIFICATION_MARGIN;
        }
    }

    private void playEntranceAnimation(Notification notification) {
        Timeline timeline = new Timeline();
        KeyValue kvOpacity = new KeyValue(notification.opacityProperty(), 1.0);
        KeyValue kvScaleX = new KeyValue(notification.scaleXProperty(), 1.0);
        KeyValue kvScaleY = new KeyValue(notification.scaleYProperty(), 1.0);

        KeyFrame kf = new KeyFrame(ANIMATION_DURATION, kvOpacity, kvScaleX, kvScaleY);
        timeline.getKeyFrames().add(kf);
        timeline.play();

        var slideIn = Animations.slideInRight(notification, ANIMATION_DURATION);
        slideIn.play();
    }

    private void playExitAnimation(Notification notification, Runnable onFinished) {
        Timeline timeline = new Timeline();
        KeyValue kvOpacity = new KeyValue(notification.opacityProperty(), 0.0);
        KeyValue kvScaleX = new KeyValue(notification.scaleXProperty(), 0.8);
        KeyValue kvScaleY = new KeyValue(notification.scaleYProperty(), 0.8);

        KeyFrame kf = new KeyFrame(ANIMATION_DURATION, kvOpacity, kvScaleX, kvScaleY);
        timeline.getKeyFrames().add(kf);

        var slideOut = Animations.slideOutRight(notification, ANIMATION_DURATION);

        timeline.setOnFinished(e -> onFinished.run());
        timeline.play();
        slideOut.play();
    }
}