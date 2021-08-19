package phone.vishnu.quotes.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import phone.vishnu.quotes.helper.AlarmHelper;
import phone.vishnu.quotes.helper.SharedPreferenceHelper;

public class StartupReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (intent.getAction() != null) {
                if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {

                    SharedPreferenceHelper sharedPreferenceHelper = new SharedPreferenceHelper(context);

                    AlarmHelper.checkAlarm(context, sharedPreferenceHelper.getAlarmString());

                    AlarmHelper.checkWidgetAlarm(context, sharedPreferenceHelper.getWidgetQuote());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}