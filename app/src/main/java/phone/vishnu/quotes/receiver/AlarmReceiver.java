package phone.vishnu.quotes.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.core.app.NotificationCompat;

import phone.vishnu.quotes.R;

public class AlarmReceiver extends BroadcastReceiver {

    private static int MID = 1002;
    String author = "";
    String message = "";

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle extras = intent.getExtras();

        message = extras.getString("message");
        author = extras.getString("author");

        long when = System.currentTimeMillis();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_quotes)
                .setContentTitle(author + " wants to say you:")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setSound(alarmSound)
                .setAutoCancel(true)
                .setWhen(when)
                .setVibrate(new long[]{1000});

        notificationManager.notify(MID, mNotifyBuilder.build());

        MID++;

    }

}