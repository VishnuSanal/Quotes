package phone.vishnu.quotes.receiver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;

import phone.vishnu.quotes.R;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String NOTIFICATION_CHANNEL_ID = "quotesNotificationChannel";
    private static int MID = 1002;
    NotificationManager notificationManager;
    String message = "Quote not found";
    String author = "Author not found";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        message = extras.getString("message");
        author = extras.getString("author");

        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

//        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, null);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel mChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Quotes Notification Channel", NotificationManager.IMPORTANCE_LOW);
//            mChannel.setDescription("Contains Notifications of Quotes");
//            mChannel.setVibrationPattern(new long[]{100});
//            notificationManager.createNotificationChannel(mChannel);
//        } else {
        createNotificationChannel(context);
            builder.setContentTitle(author + " wants to say you:")
                    .setSmallIcon(R.drawable.ic_quotes)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_LOW)
//                    .setColor(ContextCompat.getColor(context, R.color.transparent))
                    .setVibrate(new long[]{100})
//                    .setSound(alarmSound)
                    .setChannelId(NOTIFICATION_CHANNEL_ID)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                    .setWhen(System.currentTimeMillis())
                    .setAutoCancel(true);

        notificationManager.notify(MID, builder.build());
//        MID++;
    }

    void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("NOTIFICATION_CHANNEL_ID", "Quotes Notification Channel", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("Contains Notifications of Quotes");
            notificationChannel.setVibrationPattern(new long[]{100});
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}

