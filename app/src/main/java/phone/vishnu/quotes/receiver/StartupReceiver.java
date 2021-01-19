package phone.vishnu.quotes.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.Calendar;
import java.util.Date;

import phone.vishnu.quotes.helper.SharedPreferenceHelper;
import phone.vishnu.quotes.model.Quote;

import static android.content.Context.ALARM_SERVICE;

public class StartupReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        try {
            if (intent.getAction() != null) {
                if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {

                    SharedPreferenceHelper sharedPreferenceHelper = new SharedPreferenceHelper(context);
                    checkAlarm(context, sharedPreferenceHelper.getAlarmString());
                    checkWidgetAlarm(context, sharedPreferenceHelper.getWidgetQuote());
                }
            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
    }

    private void checkWidgetAlarm(Context context, Quote widgetQuote) {
        if (widgetQuote != null) {

            String QUOTE_WIDGET_UPDATE = "phone.vishnu.quotes.QUOTE_WIDGET_UPDATE";

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            Intent intent = new Intent(context, QuoteWidget.class);
            intent.setAction(QUOTE_WIDGET_UPDATE);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);

            calendar.set(Calendar.SECOND, 1);
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.add(Calendar.DAY_OF_YEAR, 1);

            if (alarmManager != null)
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        }
    }

    private void checkAlarm(Context context, String time) {
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
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

            if (alarmManager != null) {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
            }
        }
    }
}