package de.appplant.cordova.plugin.notification;

public class TriggerReceiver extends AbstractTriggerReceiver {
    public void onTrigger(Notification notification, boolean updated) {
        notification.show();
    }

    public Notification buildNotification(Builder builder) {
        return builder.build();
    }
}
