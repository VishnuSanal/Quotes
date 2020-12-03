package phone.vishnu.quotes.receiver;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import java.util.ArrayList;
import java.util.Collections;

import phone.vishnu.quotes.R;
import phone.vishnu.quotes.activity.MainActivity;
import phone.vishnu.quotes.data.QuoteData;
import phone.vishnu.quotes.data.QuoteListAsyncResponse;
import phone.vishnu.quotes.helper.SharedPreferenceHelper;
import phone.vishnu.quotes.model.Quote;

public class QuoteWidget extends AppWidgetProvider {

    private final int FAVOURITE_REQ_CODE = 1;
    private final int SHARE_REQ_CODE = 2;

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
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
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        new SharedPreferenceHelper(context).deleteWidgetQuote();
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
        if (intent != null && intent.getAction() != null && intent.getExtras() != null) {
            if ((intent.getAction().equals("phone.vishnu.quotes.WIDGET_CLICK_LISTENER"))) {
                if (intent.getExtras().containsKey("WIDGET_REQ_CODE")) {
                    onWidgetClickListener(context, intent.getExtras().getInt("WIDGET_REQ_CODE"));
                }
            } else if ((intent.getAction().equals(Intent.ACTION_DATE_CHANGED)))
                if (intent.getExtras().containsKey("WIDGET_UPDATE") && intent.getExtras().getBoolean("WIDGET_UPDATE")) {
                    initAppWidget(context);
                }
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