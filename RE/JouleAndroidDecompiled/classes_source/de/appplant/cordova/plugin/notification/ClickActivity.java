package de.appplant.cordova.plugin.notification;

public class ClickActivity extends AbstractClickActivity {
    public void onClick(Notification notification) {
        launchApp();
        if (notification.isRepeating()) {
            notification.clear();
        } else {
            notification.cancel();
        }
    }

    public Notification buildNotification(Builder builder) {
        return builder.build();
    }
}
