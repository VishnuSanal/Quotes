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

package phone.vishnu.quotes.helper;

import static android.content.Context.ALARM_SERVICE;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;
import java.util.Calendar;
import java.util.Date;
import phone.vishnu.quotes.R;
import phone.vishnu.quotes.model.Quote;
import phone.vishnu.quotes.receiver.NotificationReceiver;
import phone.vishnu.quotes.receiver.QuoteWidget;

public class AlarmHelper {

    public static void setAlarm(Context context, Calendar calendar) {

        if (calendar.getTime().compareTo(new Date()) < 0) calendar.add(Calendar.DAY_OF_MONTH, 1);

        Intent intent = new Intent(context, NotificationReceiver.class);

        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(
                        context,
                        0,
                        intent,
                        (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                                ? PendingIntent.FLAG_IMMUTABLE
                                : PendingIntent.FLAG_ONE_SHOT);

        AlarmManager alarmManager =
                (AlarmManager) context.getApplicationContext().getSystemService(ALARM_SERVICE);

        if (alarmManager != null) {
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent);
            Toast.makeText(
                            context,
                            String.format(
                                    "%s\n%s",
                                    context.getString(R.string.daily_notifications),
                                    context.getString(R.string.turned_on)),
                            Toast.LENGTH_SHORT)
                    .show();
        } else {
            Toast.makeText(
                            context,
                            String.format(
                                    "%s\n%s\n%s",
                                    context.getString(R.string.oops_something_went_wrong),
                                    context.getString(R.string.daily_notification_not_set),
                                    context.getString(R.string.try_again)),
                            Toast.LENGTH_LONG)
                    .show();
        }
    }

    public static void setDefaultAlarm(Context context) {

        Calendar calendar = Calendar.getInstance();
        // calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 30);

        if (calendar.getTime().compareTo(new Date()) < 0) calendar.add(Calendar.DAY_OF_MONTH, 1);

        Intent intent = new Intent(context.getApplicationContext(), NotificationReceiver.class);
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(
                        context.getApplicationContext(),
                        0,
                        intent,
                        (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                                ? PendingIntent.FLAG_IMMUTABLE
                                : PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        if (alarmManager != null) {
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent);
        }
    }

    public static void cancelAlarm(Context context) {
        Intent intent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(
                        context,
                        0,
                        intent,
                        (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                                ? PendingIntent.FLAG_IMMUTABLE
                                : PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
            Toast.makeText(
                            context,
                            String.format(
                                    "%s\n%s",
                                    context.getString(R.string.daily_notifications),
                                    context.getString(R.string.turned_off)),
                            Toast.LENGTH_SHORT)
                    .show();
        }
    }

    public static void checkAlarm(Context context, String time) {
        // "At " + hourOfDay + " : " + minute + " Daily"
        if (!time.equals("-1")) {

            String trim = time.replaceAll("At ", "").replaceAll(" Daily", "").trim();

            String[] split = trim.split(":");

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(split[0].trim()));
            calendar.set(Calendar.MINUTE, Integer.parseInt(split[1].trim()));

            if (calendar.getTime().compareTo(new Date()) < 0)
                calendar.add(Calendar.DAY_OF_MONTH, 1);

            Intent i = new Intent(context, NotificationReceiver.class);
            PendingIntent pendingIntent =
                    PendingIntent.getBroadcast(
                            context,
                            0,
                            i,
                            (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                                    ? PendingIntent.FLAG_IMMUTABLE
                                    : PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

            if (alarmManager != null) {
                alarmManager.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY,
                        pendingIntent);
            }
        }
    }

    public static void checkWidgetAlarm(Context context, Quote widgetQuote) {
        if (widgetQuote != null) {

            String QUOTE_WIDGET_UPDATE = "phone.vishnu.quotes.QUOTE_WIDGET_UPDATE";

            AlarmManager alarmManager =
                    (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            Intent intent = new Intent(context, QuoteWidget.class);
            intent.setAction(QUOTE_WIDGET_UPDATE);
            PendingIntent pendingIntent =
                    PendingIntent.getBroadcast(
                            context,
                            0,
                            intent,
                            (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                                    ? PendingIntent.FLAG_IMMUTABLE
                                    : 0);

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);

            calendar.set(Calendar.SECOND, 1);
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.add(Calendar.DAY_OF_YEAR, 1);

            if (alarmManager != null)
                alarmManager.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY,
                        pendingIntent);
        }
    }

    public static void scheduleWidgetUpdate(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, QuoteWidget.class);
        intent.setAction(Constants.WIDGET_UPDATE_ACTION);
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(
                        context,
                        0,
                        intent,
                        (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                                ? PendingIntent.FLAG_IMMUTABLE
                                : PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);

        calendar.set(Calendar.SECOND, 1);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DAY_OF_YEAR, 1);

        if (alarmManager != null)
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent);
    }

    public static void removeWidgetUpdate(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, QuoteWidget.class);
        intent.setAction(Constants.WIDGET_UPDATE_ACTION);
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(
                        context,
                        0,
                        intent,
                        (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                                ? PendingIntent.FLAG_IMMUTABLE
                                : PendingIntent.FLAG_UPDATE_CURRENT);

        if (alarmManager != null) alarmManager.cancel(pendingIntent);
    }
}
