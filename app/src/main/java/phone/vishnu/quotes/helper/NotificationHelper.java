package phone.vishnu.quotes.helper;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import androidx.core.app.NotificationCompat;

import java.util.Collections;

import phone.vishnu.quotes.R;
import phone.vishnu.quotes.activity.MainActivity;
import phone.vishnu.quotes.data.QuoteData;
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
        new QuoteData().getQuotes(quotes -> {
/*

            Intent intent = new Intent(mContext, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            final PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext, NOTIFICATION_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
*/

            Collections.shuffle(quotes);
            Quote quote = quotes.get(0);

            Intent shareButtonIntent = new Intent(mContext, MainActivity.class);
            shareButtonIntent.putExtra("NotificationClick", true);
            shareButtonIntent.putExtra("ShareButton", true);
            shareButtonIntent.putExtra("quote", quote.getQuote());
            shareButtonIntent.putExtra("author", quote.getAuthor());

            PendingIntent sharePendingIntent = PendingIntent.getActivity(mContext, 1, shareButtonIntent, PendingIntent.FLAG_ONE_SHOT);
            NotificationCompat.Action shareAction = new NotificationCompat.Action.Builder(R.drawable.ic_share, "Share", sharePendingIntent).build();

            Intent favButtonIntent = new Intent(mContext, MainActivity.class);
            favButtonIntent.putExtra("NotificationClick", true);
            favButtonIntent.putExtra("FavButton", true);
            favButtonIntent.putExtra("quote", quote.getQuote());
            favButtonIntent.putExtra("author", quote.getAuthor());

            PendingIntent favPendingIntent = PendingIntent.getActivity(mContext, 2, favButtonIntent, PendingIntent.FLAG_ONE_SHOT);
            NotificationCompat.Action favAction = new NotificationCompat.Action.Builder(R.drawable.ic_favorite, "Add to Favorites", favPendingIntent).build();

            final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID);
            mBuilder.setSmallIcon(R.drawable.ic_quotes)
                    .setAutoCancel(true)
                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                    .setContentIntent(sharePendingIntent)
                    .addAction(shareAction)
                    .addAction(favAction);

            NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
            bigTextStyle.setBigContentTitle("Today's Quote");
            bigTextStyle.bigText(quote.getQuote() + "\n" + " -" + quote.getAuthor());

            mBuilder.setStyle(bigTextStyle);
            mBuilder.setContentTitle(mContext.getString(R.string.app_name));

            NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, importance);
                mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
                mNotificationManager.createNotificationChannel(notificationChannel);
            }
            assert mNotificationManager != null;
            mNotificationManager.notify(NOTIFICATION_REQUEST_CODE /* Request Code */, mBuilder.build());

        });
    }
}