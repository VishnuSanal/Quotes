/*
 * Copyright (C) 2019 - 2023 Vishnu Sanal. T
 *
 * This file is part of Quotes Status Creator.
 *
 * Quotes Status Creator is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package phone.vishnu.quotes.receiver;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;
import phone.vishnu.quotes.R;
import phone.vishnu.quotes.activity.MainActivity;
import phone.vishnu.quotes.helper.AlarmHelper;
import phone.vishnu.quotes.helper.Constants;
import phone.vishnu.quotes.helper.SharedPreferenceHelper;
import phone.vishnu.quotes.model.Quote;
import phone.vishnu.quotes.repository.QuotesRepository;

public class QuoteWidget extends AppWidgetProvider {

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        AlarmHelper.scheduleWidgetUpdate(context, Constants.WIDGET_UPDATE_ACTION);
        Quote widgetQuote = new SharedPreferenceHelper(context).getWidgetQuote();
        if (widgetQuote != null) updateQuoteWidget(context, widgetQuote);
        else initAppWidget(context);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        new SharedPreferenceHelper(context).deleteWidgetQuote();
        AlarmHelper.removeWidgetUpdate(context, Constants.WIDGET_UPDATE_ACTION);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        new SharedPreferenceHelper(context).deleteWidgetQuote();
        AlarmHelper.removeWidgetUpdate(context, Constants.WIDGET_UPDATE_ACTION);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Quote widgetQuote = new SharedPreferenceHelper(context).getWidgetQuote();
        if (widgetQuote != null) updateQuoteWidget(context, widgetQuote);
        else initAppWidget(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent != null && intent.getAction() != null) {
            if ((intent.getAction().equals(Constants.WIDGET_CLICK_ACTION)))
                if (intent.getExtras() != null
                        && intent.getExtras().containsKey(Constants.WIDGET_REQ_CODE))
                    onWidgetClickListener(
                            context, intent.getExtras().getInt(Constants.WIDGET_REQ_CODE));

            if ((intent.getAction().equals(Constants.WIDGET_UPDATE_ACTION))) initAppWidget(context);
        }
    }

    private PendingIntent getPendingIntent(Context context, int REQ_CODE) {
        return PendingIntent.getBroadcast(
                context,
                REQ_CODE,
                new Intent(context, QuoteWidget.class)
                        .setAction(Constants.WIDGET_CLICK_ACTION)
                        .putExtra(Constants.WIDGET_REQ_CODE, REQ_CODE),
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                        ? PendingIntent.FLAG_IMMUTABLE
                        : PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void updateQuoteWidget(Context context, Quote quote) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.quote_widget);

        remoteViews.setTextViewText(R.id.widgetQuoteTextView, quote.getQuote());
        remoteViews.setTextViewText(
                R.id.widgetAuthorTextView, String.format("-%s", quote.getAuthor()));

        remoteViews.setOnClickPendingIntent(
                R.id.widgetShareImageView,
                getPendingIntent(context, Constants.WIDGET_SHARE_REQ_CODE));

        remoteViews.setOnClickPendingIntent(
                R.id.widgetFavoriteImageView,
                getPendingIntent(context, Constants.WIDGET_FAVOURITE_REQ_CODE));

        AppWidgetManager.getInstance(context)
                .updateAppWidget(new ComponentName(context, QuoteWidget.class), remoteViews);

        saveWidgetQuote(context, quote);
    }

    private void initAppWidget(final Context context) {

        new QuotesRepository()
                .getRandomQuote(
                        quote -> {
                            updateQuoteWidget(context, quote);
                        });
    }

    private void saveWidgetQuote(Context context, Quote quote) {
        SharedPreferenceHelper helper = new SharedPreferenceHelper(context);
        helper.saveWidgetQuote(quote);
    }

    private void onWidgetClickListener(Context context, int i) {
        if (i == Constants.WIDGET_SHARE_REQ_CODE) widgetShareButtonClicked(context);
        else if (i == Constants.WIDGET_FAVOURITE_REQ_CODE) widgetFavButtonClicked(context);
    }

    private void widgetFavButtonClicked(Context context) {
        context.startActivity(
                new Intent(context, MainActivity.class)
                        .setAction(Constants.WIDGET_FAV_ACTION)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    private void widgetShareButtonClicked(Context context) {
        context.startActivity(
                new Intent(context, MainActivity.class)
                        .setAction(Constants.WIDGET_SHARE_ACTION)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }
}
