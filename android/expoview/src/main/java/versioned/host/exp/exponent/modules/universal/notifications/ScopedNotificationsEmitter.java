package versioned.host.exp.exponent.modules.universal.notifications;

import android.content.Context;
import android.os.Bundle;

import expo.modules.notifications.notifications.emitting.NotificationsEmitter;
import expo.modules.notifications.notifications.model.Notification;
import expo.modules.notifications.notifications.model.NotificationResponse;
import host.exp.exponent.kernel.ExperienceId;

import static versioned.host.exp.exponent.modules.universal.notifications.ScopedNotificationScheduler.USER_DATA_KEY;

public class ScopedNotificationsEmitter extends NotificationsEmitter {
  private ExperienceId mExperienceId;

  public ScopedNotificationsEmitter(Context context, ExperienceId experienceId) {
    super(context);
    mExperienceId = experienceId;
  }

  @Override
  public void onNotificationReceived(Notification notification) {
    if (ScopedNotificationsUtils.shouldHandleNotification(notification, mExperienceId)) {
      super.onNotificationReceived(notification);
    }
  }

  @Override
  public void onNotificationResponseReceived(NotificationResponse response) {
    if (ScopedNotificationsUtils.shouldHandleNotification(response.getNotification(), mExperienceId)) {
      super.onNotificationResponseReceived(response);
    }
  }

  @Override
  protected void emitEvent(String event, Bundle eventData) {
    super.emitEvent(event, removeExpoData(eventData));
  }

  private Bundle removeExpoData(Bundle eventData) {
    if (eventData == null) {
      return null;
    }

    Bundle notificationBundle = eventData.getBundle("notification");
    if (notificationBundle == null) {
      notificationBundle = eventData;
    }

    Bundle contentBundle = getNestedBundle(notificationBundle, "request", "content");
    if (contentBundle != null) {
      Bundle userData = getNestedBundle(contentBundle, "data", USER_DATA_KEY);
      contentBundle.putBundle("data", userData);
    }

    return eventData;
  }

  private Bundle getNestedBundle(Bundle bundle, String... keys) {
    Bundle current = bundle;
    for (String key : keys) {
      current = current.getBundle(key);
      if (current == null) {
        break;
      }
    }
    return current;
  }
}
