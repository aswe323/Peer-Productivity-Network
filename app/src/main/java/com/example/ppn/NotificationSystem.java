package com.example.ppn;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.SystemClock;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


/**
 * uses {@link AlarmManager} to trigger notifications, allows to set notifications for a future time.
 */
public class NotificationSystem extends BroadcastReceiver {

    public static String NOTIFICATION_ID = "ID";
    public static String NOTIFICATION = "notification";

    public void onReceive(final Context context, Intent intent) {

        //Before you can deliver the notification on Android 8.0 and higher, you must register your app's notification channel with the system by passing an instance of NotificationChannel.
        // Create the NotificationChannel, but only on API 26+ because, the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "1";
            String description = "Peer Productivity Network";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("1", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = intent.getParcelableExtra(NOTIFICATION);
        int notificationId = intent.getIntExtra(NOTIFICATION_ID, 0);
        notificationManager.notify(notificationId, notification);

    }

    public static void scheduleNotification(Context context, long delay, int notificationRequestCode, Class notificationReciver, String contentTitle, String contentText) {//this method creates notification,delay is after how much time(in millis) from current time you want to schedule the notification
        //build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"1")
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_action_edit)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_edit))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        //making the notification clickable, when clicked the app will open
        Intent intent = new Intent(context, notificationReciver);
        PendingIntent activity = PendingIntent.getActivity(context, notificationRequestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT); //PendingIntent.FLAG_CANCEL_CURRENT = if i insert new notification with same notificationId then cancel the former
        //builder.setContentIntent(activity);

        Notification notification = builder.build();

        //create intent for the notification that contain the notification and it's ID, add it to an pendingIntent to create alarm that will call it after XXX milliseconds
        Intent notificationIntent = new Intent(context, NotificationSystem.class);
        notificationIntent.putExtra(NotificationSystem.NOTIFICATION_ID, notificationRequestCode);
        notificationIntent.putExtra(NotificationSystem.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationRequestCode, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT); //PendingIntent.FLAG_CANCEL_CURRENT = if i insert new notification with same notificationId then cancel the former

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    public static void cancelNotification(Context context, int notificationRequestCode){//this method cancel notification
        Intent myIntent = new Intent(context, NotificationSystem.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationRequestCode, myIntent, PendingIntent.FLAG_CANCEL_CURRENT); //PendingIntent.FLAG_CANCEL_CURRENT = if i insert new notification with same notificationId then cancel the former
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

}
