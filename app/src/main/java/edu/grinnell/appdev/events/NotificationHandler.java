package edu.grinnell.appdev.events;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class NotificationHandler{


    /**
     * TODO: Use values other that position for notification id
     * @param activity Activity from which it is called
     * @param position id of the notification
     * @param startTime Time at which the notification will go off
     * @param notification Notification object that will be passed to the alarm manager
     */
    public static void createNotification(Activity activity, int position, long startTime, Notification notification){
        Intent notificationIntent = new Intent(activity, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, position, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, startTime, pendingIntent);

    }

    /**
     * TODO: Does not seem to work. Check again
     * @param activity  Activity from which it is called
     * @param position Id of the notification
     */
    public static void deleteNotification(Activity activity, int position){
        Intent notificationIntent = new Intent(activity, NotificationPublisher.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, position, notificationIntent,0);
        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }


    public static void createNotificationChannel(Activity activity) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        NotificationChannel channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Event Channel";
            String description = "Notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            channel = new NotificationChannel("MYID", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = activity.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * Build the customized notfication
     * @param activity Activity from which it is called
     * @param title Title of the notification
     * @param content Any description of the event
     * @return Notification The final notification
     */
    public static Notification buildNotfication(Activity activity, String title, String content){
        Notification.Builder builder = new Notification.Builder(activity);
        builder.setContentTitle(title);
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable.ic_launcher);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            builder.setChannelId("MYID");
        }
        return builder.build();
    }

}

