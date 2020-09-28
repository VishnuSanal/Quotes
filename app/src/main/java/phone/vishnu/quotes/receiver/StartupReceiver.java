package phone.vishnu.quotes.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;
import java.util.Date;

import phone.vishnu.quotes.helper.SharedPreferenceHelper;

import static android.content.Context.ALARM_SERVICE;

public class StartupReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        try {
            if (intent.getAction() != null) {
                if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {

                    SharedPreferenceHelper sharedPreferenceHelper = new SharedPreferenceHelper(context);

                    final String ALARM_PREFERENCE_TIME = "customAlarmPreference";

                    // "At " + hourOfDay + " : " + minute + " Daily"
                    String time = sharedPreferenceHelper.getAlarmString();

                    if (!time.equals("-1")) {

                        String trim = time.replaceAll("At ", "").replaceAll(" Daily", "").trim();

                        String[] split = trim.split(":");

//                Log.e("vishnu", Integer.parseInt(split[0].trim()) + ":" + Integer.parseInt(split[1].trim()));

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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}