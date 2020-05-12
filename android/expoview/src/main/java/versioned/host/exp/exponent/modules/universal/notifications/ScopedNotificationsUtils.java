package versioned.host.exp.exponent.modules.universal.notifications;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import expo.modules.notifications.notifications.interfaces.NotificationTrigger;
import expo.modules.notifications.notifications.model.Notification;
import expo.modules.notifications.notifications.model.triggers.FirebaseNotificationTrigger;
import host.exp.exponent.kernel.ExperienceId;

class ScopedNotificationsUtils {
  static boolean shouldHandleNotification(Notification notification, ExperienceId currentExperienceId) {
    NotificationTrigger notificationTrigger = notification.getNotificationRequest().getTrigger();
    if (notificationTrigger instanceof FirebaseNotificationTrigger) {
      Map<String, String> data = ((FirebaseNotificationTrigger) notificationTrigger).getRemoteMessage().getData();
      if (!data.containsKey("experienceId")) {
        return false;
      }
      String experienceIdString = data.get("experienceId");
      return currentExperienceId.get().equals(experienceIdString);
    } else {
      JSONObject body = notification.getNotificationRequest().getContent().getBody();
      if (body == null) {
        return false;
      }
      try {
        String experienceId = body.getString(ScopedNotificationScheduler.EXPERIENCE_ID_KEY);
        return currentExperienceId.get().equals(experienceId);
      } catch (JSONException e) {
        Log.w("NotificationsEmitter", String.format("The notification's body should contains '%s' field.", ScopedNotificationScheduler.EXPERIENCE_ID_KEY), e);
      }
    }
    return false;
  }
}
