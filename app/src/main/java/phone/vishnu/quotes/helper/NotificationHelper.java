package phone.vishnu.quotes.helper;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import androidx.core.app.NotificationCompat;

import java.util.ArrayList;
import java.util.Collections;

import phone.vishnu.quotes.R;
import phone.vishnu.quotes.activity.MainActivity;
import phone.vishnu.quotes.data.QuoteData;
import phone.vishnu.quotes.data.QuoteListAsyncResponse;
import phone.vishnu.quotes.model.Quote;

public class NotificationHelper {

    private static final String NOTIFICATION_CHANNEL_ID = "phone.vishnu.quotes";
    private static final String NOTIFICATION_CHANNEL_NAME = "QuotesNotificationChannel";
    private static final int NOTIFICATION_REQUEST_CODE = 2222;
    private final Context mContext;

    public NotificationHelper(Context context) {
        mContext = context;
    }

    public void createNotification() {
        new QuoteData().getQuotes(new QuoteListAsyncResponse() {
            @Override
            public void processFinished(ArrayList<Quote> quotes) {

                Intent intent = new Intent(mContext, MainActivity.class);

                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                final PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext, NOTIFICATION_REQUEST_CODE /* Request code */, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext);
                mBuilder.setSmallIcon(R.drawable.ic_quotes)
                        .setAutoCancel(true)
                        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                        .setContentIntent(resultPendingIntent);

                Collections.shuffle(quotes);
                Quote quote = quotes.get(0);

                NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
                bigTextStyle.setBigContentTitle(quote.getAuthor() + " says you: ");
                bigTextStyle.bigText(quote.getQuote());

                mBuilder.setStyle(bigTextStyle);

                NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    int importance = NotificationManager.IMPORTANCE_HIGH;
                    NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, importance);
                    mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
                    mNotificationManager.createNotificationChannel(notificationChannel);
                }
                assert mNotificationManager != null;
                mNotificationManager.notify(NOTIFICATION_REQUEST_CODE /* Request Code */, mBuilder.build());

            }
        });


    }
}