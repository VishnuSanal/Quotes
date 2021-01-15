package phone.vishnu.quotes.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import phone.vishnu.quotes.R;
import phone.vishnu.quotes.activity.MainActivity;
import phone.vishnu.quotes.data.QuoteData;
import phone.vishnu.quotes.data.QuoteListAsyncResponse;
import phone.vishnu.quotes.helper.SharedPreferenceHelper;
import phone.vishnu.quotes.model.Quote;

public class QuoteWidget extends AppWidgetProvider {

    private final String QUOTE_WIDGET_UPDATE = "phone.vishnu.quotes.QUOTE_WIDGET_UPDATE";
    private final int FAVOURITE_REQ_CODE = 1;
    private final int SHARE_REQ_CODE = 2;

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        scheduleWidgetUpdate(context);
        Quote widgetQuote = new SharedPreferenceHelper(context).getWidgetQuote();
        if (widgetQuote != null)
            updateQuoteWidget(context, widgetQuote);
        else
            initAppWidget(context);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        new SharedPreferenceHelper(context).deleteWidgetQuote();
        removeWidgetUpdate(context);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        new SharedPreferenceHelper(context).deleteWidgetQuote();
        removeWidgetUpdate(context);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Quote widgetQuote = new SharedPreferenceHelper(context).getWidgetQuote();
        if (widgetQuote != null)
            updateQuoteWidget(context, widgetQuote);
        else
            initAppWidget(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent != null && intent.getAction() != null) {
            if ((intent.getAction().equals("phone.vishnu.quotes.WIDGET_CLICK_LISTENER")))
                if (intent.getExtras() != null && intent.getExtras().containsKey("WIDGET_REQ_CODE"))
                    onWidgetClickListener(context, intent.getExtras().getInt("WIDGET_REQ_CODE"));

            if ((intent.getAction().equals(QUOTE_WIDGET_UPDATE)))
                initAppWidget(context);
        }
    }

    private PendingIntent getPendingIntent(Context context, int REQ_CODE) {
        return PendingIntent.getBroadcast(
                context,
                REQ_CODE,
                new Intent(context, QuoteWidget.class)
                        .setAction("phone.vishnu.quotes.WIDGET_CLICK_LISTENER")
                        .putExtra("WIDGET_REQ_CODE", REQ_CODE),
                PendingIntent.FLAG_UPDATE_CURRENT
        );
    }

    private void updateQuoteWidget(Context context, Quote quote) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.quote_widget);

        remoteViews.setTextViewText(R.id.widgetQuoteTextView, quote.getQuote());
        remoteViews.setTextViewText(R.id.widgetAuthorTextView, String.format("-%s", quote.getAuthor()));

        remoteViews.setOnClickPendingIntent(R.id.widgetShareImageView, getPendingIntent(context, SHARE_REQ_CODE));

        remoteViews.setOnClickPendingIntent(R.id.widgetFavoriteImageView, getPendingIntent(context, FAVOURITE_REQ_CODE));

        AppWidgetManager.getInstance(context).updateAppWidget(
                new ComponentName(context, QuoteWidget.class),
                remoteViews
        );

        saveWidgetQuote(context, quote);
    }

    private void initAppWidget(final Context context) {
        new QuoteData().getQuotes(new QuoteListAsyncResponse() {
            @Override
            public void processFinished(ArrayList<Quote> quotes) {
                Collections.shuffle(quotes);
                Quote quote = quotes.get(0);

                updateQuoteWidget(context, quote);
            }
        });
    }

    private void saveWidgetQuote(Context context, Quote quote) {
        SharedPreferenceHelper helper = new SharedPreferenceHelper(context);
        helper.saveWidgetQuote(quote);
    }

    private void scheduleWidgetUpdate(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, QuoteWidget.class);
        intent.setAction(QUOTE_WIDGET_UPDATE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);

        calendar.set(Calendar.SECOND, 1);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DAY_OF_YEAR, 1);

        if (alarmManager != null)
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    private void removeWidgetUpdate(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, QuoteWidget.class);
        intent.setAction(QUOTE_WIDGET_UPDATE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (alarmManager != null)
            alarmManager.cancel(pendingIntent);
    }

    private void onWidgetClickListener(Context context, int i) {
        if (i == SHARE_REQ_CODE)
            widgetShareButtonClicked(context);
        else if (i == FAVOURITE_REQ_CODE)
            widgetFavButtonClicked(context);
    }

    private void widgetFavButtonClicked(Context context) {
        context.startActivity(new Intent(context, MainActivity.class)
                .setAction("phone.vishnu.quotes.widgetFavClicked")
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        );
    }

    private void widgetShareButtonClicked(Context context) {
        context.startActivity(new Intent(context, MainActivity.class)
                .setAction("phone.vishnu.quotes.widgetShareClicked")
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        );

    }
}